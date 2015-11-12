package org.intellij.plugins.ceylon.ide.codeInsight.resolve;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.DeclarationPsiNameIdOwner;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.ParameterDeclarationPsiIdOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonFindUsagesProvider implements FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return null;
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof DeclarationPsiNameIdOwner;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return "Please open an issue if you ever need this help :)";
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof CeylonPsi.AnyClassPsi) {
            return "class";
        } else if (element instanceof CeylonPsi.AnyInterfacePsi) {
            return "interface";
        } else if (element instanceof CeylonPsi.AttributeDeclarationPsi) {
            return "attribute";
        } else if (element instanceof CeylonPsi.AnyMethodPsi) {
            Tree.AnyMethod node = ((CeylonPsi.AnyMethodPsi) element).getCeylonNode();
            if (node.getDeclarationModel() != null && node.getDeclarationModel().isAnnotation()) {
                return "annotation";
            }
            return "function";
        } else if (element instanceof ParameterDeclarationPsiIdOwner) {
            return "function parameter";
        } else if (element instanceof CeylonPsi.TypeParameterDeclarationPsi) {
            return "type parameter";
        } else if (element instanceof CeylonPsi.ObjectDefinitionPsi) {
            return "object";
        } else if (element instanceof CeylonPsi.ConstructorPsi) {
            return "constructor";
        }

        throw new UnsupportedOperationException(element.toString());
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof CeylonPsi.AttributeDeclarationPsi) {
            return ((CeylonPsi.AttributeDeclarationPsi) element).getCeylonNode().getDeclarationModel().getQualifiedNameString();
        } else if (element instanceof CeylonPsi.ClassOrInterfacePsi) {
            Tree.Declaration ceylonNode = ((CeylonPsi.ClassOrInterfacePsi) element).getCeylonNode();
//            if (ceylonNode == null) {
//                // perhaps a stub
//                return ((CeylonPsi.ClassOrInterfacePsi) element).getQualifiedName();
//            }
            Declaration model = ceylonNode.getDeclarationModel();
            return model == null ? ceylonNode.getIdentifier().getText() : model.getQualifiedNameString();
        } else if (element instanceof CeylonPsi.AnyMethodPsi) {
            Function model = ((CeylonPsi.AnyMethodPsi) element).getCeylonNode().getDeclarationModel();
            return model == null ? ((CeylonPsi.AnyMethodPsi) element).getCeylonNode().getIdentifier().getText() : model.getQualifiedNameString();
        } else if (element instanceof CeylonPsi.ParameterDeclarationPsi) {
            return ((CeylonPsi.ParameterDeclarationPsi) element).getCeylonNode().getTypedDeclaration().getIdentifier().getText();
        } else if (element instanceof CeylonPsi.TypeParameterDeclarationPsi) {
            return ((CeylonPsi.TypeParameterDeclarationPsi) element).getCeylonNode().getIdentifier().getText();
        } else if (element instanceof CeylonFile) {
            return ((CeylonFile) element).getName();
        } else if (element instanceof CeylonPsi.ObjectDefinitionPsi) {
            return ((CeylonPsi.ObjectDefinitionPsi) element).getCeylonNode().getIdentifier().getText();
        }

        throw new UnsupportedOperationException("Descriptive name not implemented for " + element.getClass());
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof PsiNamedElement) {
            return ((PsiNamedElement) element).getName();
        }

        throw new UnsupportedOperationException();
    }
}
