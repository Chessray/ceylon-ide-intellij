import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document,
    EditorFactory
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.correct {
    SpecifyTypeQuickFix
}
import com.redhat.ceylon.model.typechecker.model {
    Type
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}
import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    IdeaLinkedMode
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

shared object ideaSpecifyTypeQuickFix satisfies IdeaSpecifyTypeQuickFix { }

shared interface IdeaSpecifyTypeQuickFix
        satisfies SpecifyTypeQuickFix<CeylonFile,Document,InsertEdit,TextEdit,
            TextChange,TextRange,Module,IdeaQuickFixData,LookupElement,IdeaLinkedMode>
                & IdeaDocumentChanges
                & IdeaQuickFix {
                
    shared default actual void newSpecifyTypeProposal(String desc, Tree.Type type,
        Tree.CompilationUnit cu, Type infType, IdeaQuickFixData data) {

        if (exists ann = data.annotation) {
            data.registerFix {
                desc = desc;
                change = null;
                image = ideaIcons.correction;
                callback = (project, editor, file) {
                    specifyType(data.doc, type, true, cu, infType);
                };
            };
        } else {
            specifyType(data.doc, type, true, cu, infType);
        }
    }
    
    shared actual void addEditableRegion(IdeaLinkedMode lm, Document doc,
        Integer start, Integer len, Integer exitSeqNumber, LookupElement[] proposals) {
        
        lm.addEditableRegion(start, len, proposals);
    }
    
    shared actual void installLinkedMode(Document doc, IdeaLinkedMode lm,
        Object owner, Integer exitSeqNumber, Integer exitPosition) {
        
        value editors = EditorFactory.instance.getEditors(doc);
        if (editors.size > 0) {
            lm.buildTemplate(editors.get(0));
        }
    }
    
    shared actual IdeaLinkedMode newLinkedMode() => IdeaLinkedMode();
    
    specifyTypeArgumentsQuickFix => ideaSpecifyTypeArgumentsQuickFix;
}

shared class IdeaSpecifyTypeIntention()
        extends AbstractIntention()
        satisfies IdeaSpecifyTypeQuickFix {

    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value declaration = nodes.findDeclaration(data.rootNode, data.node);
        addTypingProposals(data, file, declaration);
    }
    
    shared actual void newSpecifyTypeProposal(String desc, Tree.Type type, 
        Tree.CompilationUnit cu, Type infType, IdeaQuickFixData data)
            => makeAvailable(desc, null, null, (project, editor, file) {
                    specifyType(data.doc, type, true, cu, infType);
                }
            );

    familyName => "Specify type";
}
