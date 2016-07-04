import ceylon.interop.java {
    javaClass,
    createJavaObjectArray
}

import com.intellij.codeInsight {
    CodeInsightUtilCore
}
import com.intellij.lang.surroundWith {
    SurroundDescriptor,
    Surrounder
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    Condition,
    TextRange
}
import com.intellij.psi {
    PsiElement,
    PsiFile,
    PsiFileFactory,
    PsiWhiteSpace
}
import com.intellij.psi.util {
    PsiTreeUtil
}

import java.lang {
    ObjectArray,
    JString=String
}
import java.util {
    ArrayList
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonFileType
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonCompositeElement,
    CeylonPsi
}

abstract class AbstractSurrounder() satisfies Surrounder {

    isApplicable(ObjectArray<PsiElement> elements) => elements.size!=0;

    shared PsiFile createDummyFile(Project project, String content)
            => PsiFileFactory.getInstance(project)
                .createFileFromText("dummy.ceylon",
                                    CeylonFileType.instance,
                                    JString(content));

    shared PsiElement surround(ObjectArray<PsiElement> elements,
                CeylonPsi.StatementOrArgumentPsi surroundingStatement,
                CeylonPsi.BodyPsi block,
                CeylonPsi.StatementOrArgumentPsi targetPosition) {
        assert (exists firstElement = elements[0]);
        value parent = firstElement.parent;
        for (element in elements) {
            block.addBefore(element, targetPosition);
        }
        targetPosition.delete();
        value result = parent.addBefore(surroundingStatement, firstElement);
        for (element in elements) {
            element.delete();
        }
        return CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(result);
    }

}

object withIfSurrounder extends AbstractSurrounder() satisfies Surrounder {

    String content = "void a(){if(true){throw;}}";

    templateDescription => "'if' statement";

    shared actual TextRange surroundElements(Project project, Editor editor,
            ObjectArray<PsiElement> elements) {

        value file = createDummyFile(project, content);

        assert (exists ifStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
            javaClass<CeylonPsi.IfStatementPsi>(), true));
        assert (exists block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 17,
            javaClass<CeylonPsi.BlockPsi>(), true));
        assert (exists throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 18,
            javaClass<CeylonPsi.ThrowPsi>(), true));

        value formatted = surround(elements, ifStatement, block, throwStatement);

        assert (exists ic
                = PsiTreeUtil.findChildOfType(formatted,
                    javaClass<CeylonPsi.IfClausePsi>(), true));
        assert (exists cl
                = PsiTreeUtil.findChildOfType(ic,
                    javaClass<CeylonPsi.ConditionListPsi>(), true));

        value loc = cl.textOffset + 1;
        value len = cl.textLength - 2;
        return TextRange(loc, loc + len);
    }

}

object withIfElseSurrounder extends AbstractSurrounder() satisfies Surrounder {

    String content = "void a(){if(true){throw;}else{}}";

    templateDescription => "'if'/'else' statement";

    shared actual TextRange surroundElements(Project project, Editor editor,
    ObjectArray<PsiElement> elements) {

        value file = createDummyFile(project, content);

        assert (exists ifStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
            javaClass<CeylonPsi.IfStatementPsi>(), true));
        assert (exists block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 17,
            javaClass<CeylonPsi.BlockPsi>(), true));
        assert (exists throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 18,
            javaClass<CeylonPsi.ThrowPsi>(), true));

        value formatted = surround(elements, ifStatement, block, throwStatement);

        assert (exists ic
                = PsiTreeUtil.findChildOfType(formatted,
                    javaClass<CeylonPsi.IfClausePsi>(), true));
        assert (exists cl
                = PsiTreeUtil.findChildOfType(ic,
                    javaClass<CeylonPsi.ConditionListPsi>(), true));

        value loc = cl.textOffset + 1;
        value len = cl.textLength - 2;
        return TextRange(loc, loc + len);
    }

}

object withTryCatchSurrounder extends AbstractSurrounder() satisfies Surrounder {

    String content = "void a(){try{throw;}catch(e){e.printStackTrace();}}";

    templateDescription => "'try'/'catch' statement";

    shared actual TextRange surroundElements(Project project, Editor editor,
            ObjectArray<PsiElement> elements) {

        value file = createDummyFile(project, content);

        assert (exists tryStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
                    javaClass<CeylonPsi.TryCatchStatementPsi>(), true));
        assert (exists block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 12,
                    javaClass<CeylonPsi.BlockPsi>(), true));
        assert (exists throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 13,
                    javaClass<CeylonPsi.ThrowPsi>(), true));

        value formatted = surround(elements, tryStatement, block, throwStatement);

        assert (exists ct
                = PsiTreeUtil.findChildOfType(formatted,
                    javaClass<CeylonPsi.CatchClausePsi>(), true));
        assert (exists bl
                = PsiTreeUtil.findChildOfType(ct,
                    javaClass<CeylonPsi.BlockPsi>(), true));
        assert (exists pst
                = PsiTreeUtil.findChildOfType(ct,
                    javaClass<CeylonPsi.ExpressionStatementPsi>(), true));

        value loc = pst.textOffset;
        value len = pst.textLength - 1;
        return TextRange(loc, loc + len);
    }

}

object withTryFinallySurrounder extends AbstractSurrounder() satisfies Surrounder {

    String content = "void a(){try{throw;}finally{}}";

    templateDescription => "'try'/'finally' statement";

    shared actual TextRange surroundElements(Project project, Editor editor,
            ObjectArray<PsiElement> elements) {

        value file = createDummyFile(project, content);

        assert (exists tryStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
                    javaClass<CeylonPsi.TryCatchStatementPsi>(), true));
        assert (exists block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 12,
                    javaClass<CeylonPsi.BlockPsi>(), true));
        assert (exists throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 13,
                    javaClass<CeylonPsi.ThrowPsi>(), true));

        value formatted = surround(elements, tryStatement, block, throwStatement);

        assert (exists fin
                = PsiTreeUtil.findChildOfType(formatted,
                    javaClass<CeylonPsi.FinallyClausePsi>(), true));
        assert (exists bl
                = PsiTreeUtil.findChildOfType(fin,
                    javaClass<CeylonPsi.BlockPsi>(), true));

        value loc = bl.textOffset + 1;
        return TextRange(loc, loc);
    }

}

object withTryResourcesSurrounder extends AbstractSurrounder() satisfies Surrounder {

    String content = "void a(){try(){throw;}}";

    templateDescription => "'try' statement with resource list";

    shared actual TextRange surroundElements(Project project, Editor editor,
    ObjectArray<PsiElement> elements) {

        value file = createDummyFile(project, content);

        assert (exists tryStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
                    javaClass<CeylonPsi.TryCatchStatementPsi>(), true));
        assert (exists block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 14,
                    javaClass<CeylonPsi.BlockPsi>(), true));
        assert (exists throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 15,
                    javaClass<CeylonPsi.ThrowPsi>(), true));

        value formatted = surround(elements, tryStatement, block, throwStatement);

        assert (exists tc
                = PsiTreeUtil.findChildOfType(formatted,
                    javaClass<CeylonPsi.TryClausePsi>(), true));
        assert (exists rl
                = PsiTreeUtil.findChildOfType(tc,
                    javaClass<CeylonPsi.ResourceListPsi>(), true));

        value loc = rl.textOffset + 1;
        value len = rl.textLength - 2;
        return TextRange(loc, loc + len);
    }

}

object withWhileSurrounder extends AbstractSurrounder() satisfies Surrounder {

    String content = "void a(){while(true){throw;break;}}";

    templateDescription => "'while' statement";

    shared actual TextRange surroundElements(Project project, Editor editor,
    ObjectArray<PsiElement> elements) {

        value file = createDummyFile(project, content);

        assert (exists tryStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
                    javaClass<CeylonPsi.WhileStatementPsi>(), true));
        assert (exists block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 20,
                    javaClass<CeylonPsi.BlockPsi>(), true));
        assert (exists throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 21,
                    javaClass<CeylonPsi.ThrowPsi>(), true));

        value formatted = surround(elements, tryStatement, block, throwStatement);

        assert (exists wc
                = PsiTreeUtil.findChildOfType(formatted,
                    javaClass<CeylonPsi.WhileClausePsi>(), true));
        assert (exists cl
                = PsiTreeUtil.findChildOfType(wc,
                    javaClass<CeylonPsi.ConditionListPsi>(), true));

        value loc = cl.textOffset + 1;
        value len = cl.textLength - 2;
        return TextRange(loc, loc + len);
    }

}

object withForSurrounder extends AbstractSurrounder() satisfies Surrounder {

    String content = "void a(){for(i in 0..0){throw;}}";

    templateDescription => "'for' statement";

    shared actual TextRange surroundElements(Project project, Editor editor,
    ObjectArray<PsiElement> elements) {

        value file = createDummyFile(project, content);

        assert (exists forStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
                    javaClass<CeylonPsi.ForStatementPsi>(), true));
        assert (exists block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 23,
                    javaClass<CeylonPsi.BlockPsi>(), true));
        assert (exists throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 24,
                    javaClass<CeylonPsi.ThrowPsi>(), true));

        value formatted = surround(elements, forStatement, block, throwStatement);

        assert (exists fr
                = PsiTreeUtil.findChildOfType(formatted,
                    javaClass<CeylonPsi.ForClausePsi>(), true));
        assert (exists it
                = PsiTreeUtil.findChildOfType(fr,
                    javaClass<CeylonPsi.ForIteratorPsi>(), true));

        value loc = it.textOffset + 1;
        value len = it.textLength - 2;
        return TextRange(loc, loc + len);
    }

}


shared class CeylonSurroundDescriptor() satisfies SurroundDescriptor {

    value condition = object satisfies Condition<PsiElement> {
        \ivalue(PsiElement element) => element is CeylonPsi.StatementPsi
                                    && !element is CeylonPsi.VariablePsi
                                                 | CeylonPsi.TypeParameterDeclarationPsi;
    };

    shared actual ObjectArray<PsiElement> getElementsToSurround(PsiFile file,
            Integer selectionStart, Integer selectionEnd) {

        value startElem = file.findElementAt(selectionStart);
        value endElem = file.findElementAt(selectionEnd);
        if (!exists startElem) {
            return PsiElement.emptyArray;
        }
        if (!exists endElem) {
            return PsiElement.emptyArray;
        }

        value start
                = if (is PsiWhiteSpace startElem)
                then PsiTreeUtil.getNextSiblingOfType(startElem,
                        javaClass<CeylonCompositeElement>())
                else startElem;
        value end
                = if (is PsiWhiteSpace endElem)
                then PsiTreeUtil.getPrevSiblingOfType(endElem,
                        javaClass<CeylonCompositeElement>())
                else endElem;

        value first = PsiTreeUtil.findFirstParent(start, condition);
        value last = PsiTreeUtil.findFirstParent(end, condition);
        if (!is CeylonPsi.StatementPsi first) {
            return PsiElement.emptyArray;
        }
        if (!is CeylonPsi.StatementPsi last) {
            return PsiElement.emptyArray;
        }

        value list = ArrayList<PsiElement>();
        variable CeylonPsi.StatementPsi? current = first;
        while (exists statement = current, statement!=last) {
            list.add(statement);
            current = PsiTreeUtil.getNextSiblingOfType(current,
                        javaClass<CeylonPsi.StatementPsi>());
        }
        list.add(last);
        return list.toArray(PsiElement.emptyArray);
    }

    surrounders => createJavaObjectArray {
        withIfSurrounder,
        withIfElseSurrounder,
        withTryCatchSurrounder,
        withTryFinallySurrounder,
        withTryResourcesSurrounder,
        withForSurrounder,
        withWhileSurrounder
    };

    exclusive => false;
}
