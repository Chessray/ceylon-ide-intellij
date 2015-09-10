import ceylon.interop.java {
    Iter=CeylonIterable
}

import com.intellij.codeInsight.completion {
    CompletionParameters,
    CompletionResultSet,
    InsertHandler
}
import com.intellij.codeInsight.lookup {
    LookupElementBuilder,
    LookupElement
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.util {
    ProcessingContext,
    PlatformIcons
}
import com.redhat.ceylon.cmr.api {
    ModuleVersionDetails,
    ModuleSearchResult
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    Node
}
import com.redhat.ceylon.ide.common.completion {
    IdeCompletionManager,
    appendTypeParameters,
    getRefinementTextFor,
    getNamedInvocationTextFor,
    getTextFor,
    getInlineFunctionTextFor
}
import com.redhat.ceylon.ide.common.model {
    CeylonProject
}
import com.redhat.ceylon.ide.common.typechecker {
    LocalAnalysisResult
}
import com.redhat.ceylon.ide.common.util {
    OccurrenceLocation,
    ProgressMonitor
}
import com.redhat.ceylon.model.typechecker.model {
    Function,
    Value,
    Declaration,
    Class,
    Interface,
    TypeAlias,
    Unit,
    Scope,
    FunctionOrValue,
    Type,
    Reference,
    ClassOrInterface,
    Package
}

import java.awt {
    Font
}
import java.util {
    JList=List
}
import java.util.regex {
    Pattern
}

import javax.swing {
    Icon
}

import org.antlr.runtime {
    CommonToken
}
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    ceylonHighlightingColors,
    textAttributes
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

shared class CompletionData(PhasedUnit pu, Document doc, TypeChecker tc) satisfies LocalAnalysisResult<Document,Module> {
    shared actual Tree.CompilationUnit rootNode => pu.compilationUnit;
    shared actual PhasedUnit phasedUnit => pu;
    shared actual Document document => doc;
    shared actual JList<CommonToken>? tokens => pu.tokens;
    shared actual TypeChecker typeChecker => tc;
    shared actual CeylonProject<Module>? ceylonProject => null; // TODO
}

shared object ideaCompletionManager extends IdeCompletionManager<CompletionData,Module,LookupElement,Document>() {
    
    shared void addCompletions(CompletionParameters parameters, ProcessingContext context, CompletionResultSet result,
            PhasedUnit pu, TypeChecker tc) {
        value isSecondLevel = parameters.invocationCount >= 2;
        value element = parameters.originalPosition;
        value doc = parameters.editor.document;
        value params = CompletionData(pu, doc, tc);
        value line = doc.getLineNumber(element.textOffset);
        
        value monitor = object satisfies ProgressMonitor {
            shared actual variable Integer workRemaining = 100;
            shared actual void worked(Integer amount) {
                workRemaining -= amount;
            }
            shared actual void subTask(String? desc) {}
        };
        value returnedParamInfo = true; // The parameters tooltip has nothing to do with code completion, so we bypass it
        value completions = getContentProposals(params, parameters.editor.caretModel.offset, line, isSecondLevel, monitor, returnedParamInfo);
        
        //MySimpleColoredComponent.patch(CompletionService.completionService.currentCompletion);
        
        for (completion in completions) {
            result.addElement(completion);
        }
        
        if (!isSecondLevel) {
            result.addLookupAdvertisement("Call again to toggle second-level completions");
        }
    }
    
    shared actual List<Pattern> proposalFilters => empty;
    shared actual Boolean showParameterTypes => true;
    shared actual String inexactMatches => "positional";
    shared actual Boolean supportsLinkedModeInArguments => false;

    shared actual LookupElement newParametersCompletionProposal(Integer offset,
        Type type, JList<Type> argTypes, Node node, CompletionData data) {
        
        print("newParametersCompletionProposal");
        return MyLookupElementBuilder(type.declaration, node.unit, true).lookupElement;
    }
    
    shared actual String getDocumentSubstring(Document doc, Integer start, Integer length)
            => doc.getText(TextRange.from(start, length));
    
    shared actual LookupElement newPositionalInvocationCompletion(Integer offset, String prefix,
        Declaration dec, Reference? pr, Scope scope, CompletionData data, Boolean isMember,
        OccurrenceLocation? ol, String? typeArgs, Boolean includeDefaulted, Declaration? qualifyingDec) {
        
        //print("newPositionalInvocationCompletion ``dec`` - ``typeArgs else "null"``");
        return MyLookupElementBuilder(dec, dec.unit, true, typeArgs, qualifyingDec).lookupElement;
    }
    
    shared actual LookupElement newNamedInvocationCompletion(Integer offset, String prefix,
        Declaration dec, Reference? pr, Scope scope, CompletionData data, Boolean isMember,
        OccurrenceLocation? ol, String? typeArgs, Boolean includeDefaulted) {
        
        //print("newNamedInvocationCompletion");
        assert(exists pr);
        String text = getNamedInvocationTextFor(dec, pr, data.rootNode.unit, includeDefaulted, typeArgs);
        
        // TODO linked mode in parameters (see InvocationCompletionProposal.activeLinkedMode)
        return LookupElementBuilder.create(text)
            .withIcon(PlatformIcons.\iMETHOD_ICON);
    }
    
    shared actual LookupElement newReferenceCompletion(Integer offset, String prefix,
        Declaration dec, Unit u, Reference? pr, Scope scope, CompletionData data,
        Boolean isMember, Boolean includeTypeArgs) {
        
        //print("newReferenceCompletion ``includeTypeArgs``");
        String args;
        if (includeTypeArgs) {
            value sb = StringBuilder();
            appendTypeParameters(dec, pr, u, sb, false);
            args = sb.string.replace("&lt;", "<").replace("&gt;", ">");
        } else {
            args = "";
        }
        return MyLookupElementBuilder(dec, dec.unit, false, args).lookupElement;
    }
    
    shared actual LookupElement newRefinementCompletionProposal(Integer offset, String prefix,
        Declaration dec, Reference? pr, Scope scope, CompletionData data, Boolean isInterface,
        ClassOrInterface ci, Node node, Unit unit, Document doc, Boolean preamble) {
        
        value ref = getRefinedProducedReference(scope, dec);
        value text = getRefinementTextFor(dec, ref, unit, isInterface, ci, "", false, preamble);
        
        //print("newRefinementCompletionProposal");
        return LookupElementBuilder.create("", text)
            .withPresentableText("")
            .withIcon(ideaIcons.refinement)
            .withInsertHandler(putCaretInBracesInsertHandler)
            .withRenderer(ColoredTailElementRenderer.\iINSTANCE);
    }
    
    shared actual LookupElement newMemberNameCompletionProposal(Integer offset, String prefix, String name, String unquotedName) {
        //print("newMemberNameCompletionProposal");
        
        return LookupElementBuilder.create(unquotedName, name)
            .withIcon(ideaIcons.local);
    }
    
    shared actual LookupElement newKeywordCompletionProposal(Integer offset, String prefix, String keyword) {
        value attr = textAttributes(ceylonHighlightingColors.keyword);
        
        return LookupElementBuilder.create(keyword)
            .withItemTextForeground(attr.foregroundColor)
            .withBoldness(attr.fontType.and(Font.\iBOLD) != 0);
    }
    
    shared actual LookupElement newAnonFunctionProposal(Integer offset, Type? requiredType,
        Unit unit, String text, String header, Boolean isVoid) {
        
        //print("newAnonFunctionProposal");
        
        // TODO should appear at the top of the list
        // TODO select "nothing" or place caret inside braces
        return LookupElementBuilder.create(text)
            .withIcon(ideaIcons.anonymousFunction);
    }
    
    shared actual LookupElement newNamedArgumentProposal(Integer offset, String prefix,
        CompletionData data, Tree.CompilationUnit cu, Declaration dec, Scope scope) {
        
        //print("newNamedArgumentProposal");
        // TODO select "nothing"
        // TODO should appear at the top of the completion list
        value text = getTextFor(dec, cu.unit) + " = nothing";
        return LookupElementBuilder.create(text)
            .withIcon(ideaIcons.param);
    }
    
    shared actual LookupElement newInlineFunctionProposal(Integer offset, FunctionOrValue dec,
        Scope scope, Node node, String prefix, CompletionData data, Document doc) {

        //print("newInlineFunctionProposal");
        value p = dec.initializerParameter;
        value unit = node.unit;
        value text = getInlineFunctionTextFor(p, null, unit, "");
        
        // TODO select "nothing"
        // TODO should appear at the top of the completion list
        return LookupElementBuilder.create(text)
            .withIcon(ideaIcons.param);
    }
    
    shared actual LookupElement newProgramElementReferenceCompletion(Integer offset, String prefix,
        Declaration dec, Unit? u, Reference? pr, Scope scope, CompletionData data, Boolean isMember) {
        
        //print("newProgramElementReferenceCompletion");
        return MyLookupElementBuilder(dec, dec.unit, false).lookupElement;
    }
    
    shared actual LookupElement newBasicCompletionProposal(Integer offset, String prefix,
        String text, String escapedText, Declaration decl, CompletionData data) {

        //print("newBasicCompletionProposal");
        return LookupElementBuilder.create(escapedText)
            .withPresentableText(text);
    }
    
    shared actual LookupElement newPackageDescriptorProposal(Integer offset, String prefix, String packageName) {
        return LookupElementBuilder.create(packageName);
    }
    
    shared actual LookupElement newCurrentPackageProposal(Integer offset, String prefix, String packageName, CompletionData data) {
        return LookupElementBuilder.create(packageName); // TODO icon (module or package)
    }

    shared actual LookupElement newImportedModulePackageProposal(Integer offset, String prefix,
        String memberPackageSubname, Boolean withBody,
        String fullPackageName, CompletionData data,
        Package candidate) {
        
        value text = if (withBody) then "``memberPackageSubname`` { ... }" else memberPackageSubname;
        
        return LookupElementBuilder.create(text).withIcon(ideaIcons.packages);
    }
    
    shared actual LookupElement newQueriedModulePackageProposal(Integer offset, String prefix,
        String memberPackageSubname, Boolean withBody,
        String fullPackageName, CompletionData data,
        ModuleVersionDetails version, Unit unit, ModuleSearchResult.ModuleDetails md) {
     
        print("newQueriedModulePackageProposal");
        return LookupElementBuilder.create(memberPackageSubname);
    }       
    
    shared actual LookupElement newModuleProposal(Integer offset, String prefix, Integer len, 
        String versioned, ModuleSearchResult.ModuleDetails mod,
        Boolean withBody, ModuleVersionDetails version, String name, Node node) {
        
        //print("newModuleProposal");
        // TODO linked mode in version
        return LookupElementBuilder.create(versioned)
                .withIcon(ideaIcons.modules);
    }
    
    shared actual LookupElement newModuleDescriptorProposal(Integer offset, String prefix, String name) {
        return LookupElementBuilder.create(name)
                .withIcon(ideaIcons.modules);
    }

    shared actual LookupElement newJDKModuleProposal(Integer offset, String prefix, Integer len, 
        String versioned, String name) {

        //print("newJDKModuleProposal");
        return LookupElementBuilder.create(versioned)
                .withIcon(ideaIcons.modules);
    }

    // Not supported in IntelliJ (see CeylonParameterInfoHandler instead)
    shared actual LookupElement newParameterInfo(Integer offset, Declaration dec, 
        Reference producedReference, Scope scope, CompletionData data, Boolean namedInvocation) => nothing;

    shared actual LookupElement newFunctionCompletionProposal(Integer offset, String prefix,
        String text, Declaration dec, Unit unit, CompletionData data) {
        
        return LookupElementBuilder.create("``dec.getName(unit)``(...)")
            .withIcon(ideaIcons.surround)
            .withInsertHandler(ReplaceTextHandler(offset - prefix.size, offset, text));
    }

}

class MyLookupElementBuilder(Declaration decl, Unit unit, Boolean allowInvocation, 
        String? typeArgs = null, Declaration? parentDecl = null) {
    
    String text = (if (exists name = parentDecl?.nameAsString) 
                  then "``name``.``decl.nameAsString``"
                  else decl.nameAsString)
                + (typeArgs else "");
    
    variable String tailText = "";
    variable Boolean grayTailText = false;
    variable Icon? icon = null;
    variable String? typeText = null;
    variable InsertHandler<LookupElement>? handler = null;
    
    void visitFunction(Function fun) {
        if (fun.annotation) {
            icon = PlatformIcons.\iANNOTATION_TYPE_ICON;
        } else {
            icon = PlatformIcons.\iMETHOD_ICON;
            
            if (allowInvocation) {
                value params = Iter(fun.firstParameterList.parameters).map((p) => p.type.declaration.name + " " + p.name);
                tailText = "(``", ".join(params)``)";
                typeText = if (fun.declaredVoid) then "void" else fun.typeDeclaration.name;
                handler = functionInsertHandler;
            } else {
                handler = declarationInsertHandler;
            }
        }
    }
    
    void visitValue(Value val) {
        if (is Class t = val.type?.declaration, t.name.first?.lowercase else false) {
            icon = PlatformIcons.\iANONYMOUS_CLASS_ICON;
            handler = declarationInsertHandler;
        } else {
            icon = PlatformIcons.\iPROPERTY_ICON;
            typeText = val.typeDeclaration?.name;
        }
    }
    
    void visitClass(Class klass) {
        icon = PlatformIcons.\iCLASS_ICON;
        tailText = " (``klass.container.qualifiedNameString``)";
        grayTailText = true;
        handler = declarationInsertHandler;
    }
    
    void visitInterface(Interface int) {
        icon = PlatformIcons.\iINTERFACE_ICON;
        tailText = " (``int.container.qualifiedNameString``)";
        grayTailText = true;
        handler = declarationInsertHandler;
    }
    
    void visitAlias(TypeAlias typeAlias) {
        print("alias");
        // TODO create an icon for aliases
    }
    
    void visit(Declaration decl) {
        if (is Function decl) {
            visitFunction(decl);
        } else if (is Value decl) {
            visitValue(decl);
        } else if (is Class decl) {
            visitClass(decl);
        } else if (is Interface decl) {
            visitInterface(decl);
        } else if (is TypeAlias decl) {
            visitAlias(decl);
        }
    }
    
    visit(decl);
    
    shared LookupElement lookupElement = LookupElementBuilder.create([decl, unit], text)
        .withTailText(tailText, grayTailText)
        .withTypeText(typeText)
        .withIcon(icon)
        .withInsertHandler(handler);
}
