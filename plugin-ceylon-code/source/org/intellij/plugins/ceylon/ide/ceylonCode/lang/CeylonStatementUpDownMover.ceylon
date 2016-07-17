import ceylon.interop.java {
    javaClass
}

import com.intellij.codeInsight.editorActions.moveUpDown {
    LineRange,
    StatementUpDownMover
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.util {
    Condition
}
import com.intellij.psi {
    PsiElement,
    PsiFile
}
import com.intellij.psi.util {
    PsiTreeUtil {
        ...
    }
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}

shared class CeylonStatementUpDownMover() extends StatementUpDownMover() {

    value statementClass = javaClass<CeylonPsi.StatementOrArgumentPsi>();

    object condition satisfies Condition<PsiElement> {
        \ivalue(PsiElement element)
                => element is CeylonPsi.StatementOrArgumentPsi
                && !element is CeylonPsi.VariablePsi
                             | CeylonPsi.TypeParameterDeclarationPsi
                             | CeylonPsi.ForIteratorPsi;
    }

    shared actual Boolean checkAvailable(Editor editor, PsiFile file, MoveInfo moveInfo, Boolean down) {

        value pair = getElementRange(editor, file, getLineRangeFromSelection(editor));
        if (!exists pair) {
            return false;
        }

        value first = findFirstParent(pair.first, condition);
        value last = findFirstParent(pair.second, condition);
        if (!is CeylonPsi.StatementOrArgumentPsi first) {
            return false;
        }
        if (!is CeylonPsi.StatementOrArgumentPsi last) {
            return false;
        }

        if (exists other = if (down)
                then getNextSiblingOfType(last, statementClass)
                else getPrevSiblingOfType(first, statementClass)) {
            moveInfo.toMove = LineRange(first, last);
            moveInfo.toMove2 = LineRange(other);
            return true;
        }
        else {
            return false;
        }
    }
}
