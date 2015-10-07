import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.ide.common.correct {
    ChangeReferenceQuickFix
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

object ideaChangeReferenceQuickFix
        satisfies ChangeReferenceQuickFix<CeylonFile,TypeChecker,Document,InsertEdit,TextEdit,TextChange,IdeaQuickFixData,TextRange,LookupElement> 
                & IdeaDocumentChanges & AbstractIntention {
    
    shared actual void newChangeReferenceProposal(IdeaQuickFixData data, String desc, TextChange change, TextRange selection) {
        data.registerFix(desc, change, selection, ideaIcons.correction, true);
    }
}
