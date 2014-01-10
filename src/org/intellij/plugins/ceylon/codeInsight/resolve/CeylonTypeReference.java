package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.psi.CeylonPsi;
import org.jetbrains.annotations.Nullable;

public class CeylonTypeReference<T extends PsiElement> extends CeylonReference<T> {

    public CeylonTypeReference(T element, TextRange range, boolean soft) {
        super(element, range, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        final PsiElement spr = super.resolve();
        if (spr != null) {
            return spr;
        }

        Tree.Identifier node = ((CeylonPsi.IdentifierPsi) myElement).getCeylonNode();
        if (node == null) {
            return null;
        }
        final String nodeText = node.getText();

        // Try using ClassIndex
        PsiElement resolved = resolveByFqn(nodeText);
        if (resolved != null) {
            return resolved;
        }

        // Try implicit ceylon.language.*
        String fqn = declaration.getContainer().getQualifiedNameString() + "." + nodeText;
        resolved = JavaPsiFacade.getInstance(myElement.getProject()).findClass(fqn, GlobalSearchScope.allScope(myElement.getProject()));

        if (resolved != null) {
            return resolved;
        }

        fqn += "_";
        return JavaPsiFacade.getInstance(myElement.getProject()).findClass(fqn, GlobalSearchScope.allScope(myElement.getProject()));
    }

}
