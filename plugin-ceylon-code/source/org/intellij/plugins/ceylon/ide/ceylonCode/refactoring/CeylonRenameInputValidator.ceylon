import ceylon.interop.java {
    javaClass
}

import com.intellij.openapi.project {
    Project
}
import com.intellij.patterns {
    ElementPattern,
    PlatformPatterns
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.refactoring.rename {
    RenameInputValidatorEx
}
import com.intellij.util {
    ProcessingContext
}
import com.redhat.ceylon.ide.common.util {
    escaping
}

import java.lang {
    JString=String
}
import java.util.regex {
    Pattern
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}
import com.intellij.lang.refactoring {
    NamesValidator
}

shared class CeylonRenameTypeInputValidator() satisfies RenameInputValidatorEx {

    shared actual String? getErrorMessage(String newName, Project project) {
        if (newName.empty) {
            return "missing identifier";
        }
        if (!Pattern.matches("^[a-zA-Z_]\\w*$", JString(newName))) {
            return "'``newName``' is not a legal identifier";
        }
//        if (escaping.isKeyword(newName)) {
//            return "'``newName``' is a keyword";
//        }
        assert (exists first = newName[0]);
        if (!first.uppercase) {
            return "'``newName``' does not begin with uppercase";
        }
        return null;
    }

    isInputValid(String newName, PsiElement element, ProcessingContext processingContext) => true;

    shared actual ElementPattern<out PsiElement> pattern
            => PlatformPatterns.psiElement(javaClass<CeylonPsi.TypeDeclarationPsi>());

}

shared class CeylonRenameTypedInputValidator() satisfies RenameInputValidatorEx {

    shared actual String? getErrorMessage(String newName, Project project) {
        if (newName.empty) {
            return "missing identifier";
        }
        if (!Pattern.matches("^[a-zA-Z_]\\w*$", JString(newName))) {
            return "'``newName``' is not a legal identifier";
        }
        if (escaping.isKeyword(newName)) {
            return "'``newName``' is a keyword";
        }
        assert (exists first = newName[0]);
        if (!first.lowercase && !first=='_') {
            return "'``newName``' does not begin with lowercase";
        }
        return null;
    }

    isInputValid(String newName, PsiElement element, ProcessingContext processingContext) => true;

    shared actual ElementPattern<out PsiElement> pattern
            => PlatformPatterns.psiElement(javaClass<CeylonPsi.TypedDeclarationPsi>());

}

shared class CeylonNamesValidator() satisfies NamesValidator {

    isIdentifier(String string, Project project)
            => !string.empty
            && Pattern.matches("^[a-zA-Z_]\\w*$", JString(string))
            && !escaping.isKeyword(string);

    isKeyword(String string, Project project)
            => escaping.isKeyword(string);

}