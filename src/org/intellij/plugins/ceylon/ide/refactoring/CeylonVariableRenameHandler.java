package org.intellij.plugins.ceylon.ide.refactoring;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
import com.intellij.refactoring.rename.inplace.VariableInplaceRenameHandler;
import com.intellij.refactoring.rename.inplace.VariableInplaceRenamer;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonVariableRenameHandler extends VariableInplaceRenameHandler {

    @Nullable
    @Override
    protected VariableInplaceRenamer createRenamer(@NotNull final PsiElement elementToRename, final Editor editor) {
        final PsiFile file = elementToRename.getContainingFile();

        return new VariableInplaceRenamer((PsiNamedElement)elementToRename, editor) {
            @Override
            public void finish(boolean success) {
                super.finish(success);

                if (success && file instanceof CeylonFile) {
                    ((CeylonFile) file).forceReparse();
                }
            }
        };
    }

    @Override
    protected boolean isAvailable(PsiElement element, Editor editor, PsiFile file) {
        PsiElement context = file.findElementAt(editor.getCaretModel().getOffset());

        if (context != null && context.getContainingFile() != element.getContainingFile()) return false;

        if (!(element instanceof PsiNameIdentifierOwner)) {
            return false;
        }
        return true;
    }
}
