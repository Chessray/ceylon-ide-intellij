import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.psi {
    PsiDocumentManager
}
import com.redhat.ceylon.ide.common.completion {
    RefinementCompletionProposal
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.model.typechecker.model {
    Reference,
    Declaration,
    Scope
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

class IdeaRefinementCompletionProposal(Integer offset, String prefix, Reference pr,
        String desc, String text, CompletionData data,
        Declaration dec, Scope scope, Boolean fullType, Boolean explicitReturnType)
        extends RefinementCompletionProposal<LookupElement>
        (offset, prefix, pr, desc, text, data, dec, scope, fullType, explicitReturnType) 
        satisfies IdeaCompletionProposal {

    shared LookupElement lookupElement => newLookup(desc, text, ideaIcons.forDeclaration(dec),
        object satisfies InsertHandler<LookupElement> {
            shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? t) {
                // Undo IntelliJ's completion
                value platformDoc = data.commonDocument;
                replaceInDoc(platformDoc, offset, text.size - prefix.size, "");
                PsiDocumentManager.getInstance(data.editor.project).commitDocument(platformDoc.nativeDocument);
                
                value change = createChange(platformDoc);
                
                object extends WriteCommandAction<DefaultRegion?>(data.editor.project, data.file) {
                    shared actual void run(Result<DefaultRegion?> result) {
                        change.apply();
                    }
                }.execute();
                
                adjustSelection(data);
                enterLinkedMode(platformDoc);
            }
        }
    );
    
    shared actual LookupElement newNestedCompletionProposal(Declaration dec, Integer loc) {
        value unit = data.lastCompilationUnit.unit;
        value name = getNestedCompletionText(false, unit, dec);
        value desc = getNestedCompletionText(true, unit, dec);
        
        return newLookup(desc, name, ideaIcons.forDeclaration(dec));
    }
    
    shared actual LookupElement newNestedLiteralCompletionProposal(String val, Integer loc) {
        return newLookup(val, val, ideaIcons.correction);
    }
    
    shared actual Boolean toggleOverwrite => false;
}
