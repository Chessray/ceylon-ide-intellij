package org.intellij.plugins.ceylon.ide.hierarchy;

import com.intellij.ide.hierarchy.HierarchyBrowser;
import com.intellij.ide.hierarchy.HierarchyProvider;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonTypeHierarchyProvider implements HierarchyProvider {

    @Override
    public void browserActivated(@NotNull HierarchyBrowser hierarchyBrowser) {
        ((CeylonTypeHierarchyBrowser)hierarchyBrowser)
                .changeView(CeylonTypeHierarchyBrowser.TYPE_HIERARCHY_TYPE);
    }

    @Nullable
    @Override
    public PsiElement getTarget(@NotNull DataContext dataContext) {
        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        if (project == null) return null;

        PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        CeylonPsi.DeclarationPsi psi = PsiTreeUtil.getParentOfType(element, CeylonPsi.ClassOrInterfacePsi.class, false);
        if (psi==null) psi = PsiTreeUtil.getParentOfType(element, CeylonPsi.ObjectDefinitionPsi.class, false);
        return psi;
    }

    @NotNull
    @Override
    public HierarchyBrowser createHierarchyBrowser(PsiElement psiElement) {
        return new CeylonTypeHierarchyBrowser(psiElement.getProject(), psiElement);
    }
}
