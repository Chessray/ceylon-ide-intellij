package org.intellij.plugins.ceylon.ide.codeInsight.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.util.FindDeclarationNodeVisitor;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Unit;
import org.intellij.plugins.ceylon.ide.psi.*;
import org.intellij.plugins.ceylon.ide.psi.stub.ClassIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static com.redhat.ceylon.ide.util.Nodes.getReferencedExplicitDeclaration;

public class CeylonReference<T extends PsiElement> extends PsiReferenceBase<T> {
    protected Declaration declaration;

    public CeylonReference(T element, TextRange range, boolean soft) {
        super(element, range, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (!(myElement instanceof CeylonPsi.IdentifierPsi)) {
            throw new UnsupportedOperationException();
        }

        // Try using the type checker
        Tree.CompilationUnit compilationUnit = ((CeylonFile) myElement.getContainingFile()).getCompilationUnit();

        Node parentNode = ((CeylonCompositeElement) myElement.getParent()).getCeylonNode();

        if (parentNode instanceof Tree.InvocationExpression) {
            parentNode = ((Tree.InvocationExpression) parentNode).getPrimary();
        }
        declaration = getReferencedExplicitDeclaration(parentNode, compilationUnit);
        if (declaration == null) {
            return null;
        }

        Unit unit = declaration.getUnit();
        PsiFile containingFile = myElement.getContainingFile();

        if (unit != compilationUnit.getUnit()) {
            String protocol = unit.getFullPath().contains("!/") ? "jar://" : "file://";
            VirtualFile vfile = VirtualFileManager.getInstance().findFileByUrl(protocol + unit.getFullPath());
            if (vfile != null) {
                containingFile = PsiManager.getInstance(myElement.getProject()).findFile(vfile);
                if (containingFile instanceof CeylonFile) {
                    compilationUnit = ((CeylonFile) containingFile).getCompilationUnit();
                }
            }
        }

        FindDeclarationNodeVisitor visitor = new FindDeclarationNodeVisitor(declaration);
        compilationUnit.visit(visitor);
        Tree.Declaration declarationNode = visitor.getDeclarationNode();

        if (declarationNode != null) {
            return CeylonTreeUtil.findPsiElement(declarationNode, containingFile);
        }
        return containingFile;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return PsiElement.EMPTY_ARRAY;
    }

    protected PsiElement resolveByFqn(String fqn) {
        Collection<CeylonClass> decls = ClassIndex.getInstance().get(fqn, myElement.getProject(),
                GlobalSearchScope.allScope(myElement.getProject()));

        if (!decls.isEmpty()) {
            return decls.iterator().next();
        }

        return null;
    }
}
