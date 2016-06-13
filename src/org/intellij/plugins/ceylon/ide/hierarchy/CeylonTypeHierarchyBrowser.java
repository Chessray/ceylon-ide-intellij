package org.intellij.plugins.ceylon.ide.hierarchy;

import ceylon.interop.java.JavaCollection;
import com.intellij.ide.hierarchy.HierarchyBrowserManager;
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.ide.hierarchy.TypeHierarchyBrowserBase;
import com.intellij.ide.util.treeView.AlphaComparator;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.ide.util.treeView.SourceComparator;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.module.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.util.CompositeAppearance;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.IdeModule;
import com.redhat.ceylon.model.typechecker.model.*;
import com.redhat.ceylon.model.typechecker.model.Module;
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting.highlighter_;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.descriptions_;
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve.CeylonReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class CeylonTypeHierarchyBrowser extends TypeHierarchyBrowserBase {

    private Project project;

    CeylonTypeHierarchyBrowser(Project project, PsiElement element) {
        super(project, element);
        this.project = project;
    }

    static Set<PhasedUnit> collectPhasedUnits(Project project) {
        Set<PhasedUnit> result = new HashSet<>();
        IdeaCeylonProjects ceylonProjects = project.getComponent(IdeaCeylonProjects.class);
        for (com.intellij.openapi.module.Module mod: ModuleManager.getInstance(project).getModules()) {
            CeylonProject<com.intellij.openapi.module.Module, VirtualFile, VirtualFile, VirtualFile> cp
                    = ceylonProjects.getProject(mod);
            if (cp!=null) {
                CeylonProject.Modules modules = cp.getModules();
                TypeChecker typechecker = cp.getTypechecker();
                if (typechecker != null) {
                    result.addAll(typechecker.getPhasedUnits().getPhasedUnits());
                }
                for (Module m : modules.getTypecheckerModules().getListOfModules()) {
                    result.addAll(new JavaCollection<PhasedUnit>(null,
                            ((IdeModule) m).getPhasedUnits().sequence()));
                }
            }
        }

        return result;
    }

    @Nullable
    @Override
    protected HierarchyTreeStructure createHierarchyTreeStructure(@NotNull String typeName, @NotNull PsiElement psiElement) {
        final CeylonPsi.ClassOrInterfacePsi element =
                (CeylonPsi.ClassOrInterfacePsi) psiElement;
        if (SUPERTYPES_HIERARCHY_TYPE.equals(typeName)) {
            return new SupertypesHierarchyTreeStructure(element);
        }
        else if (SUBTYPES_HIERARCHY_TYPE.equals(typeName)) {
            return new SubtypesHierarchyTreeStructure(element);
        }
        else if (TYPE_HIERARCHY_TYPE.equals(typeName)) {
            return new TypeHierarchyTreeStructure(element);
        }
        else {
            return null;
        }
    }

    @Override
    protected void createTrees(@NotNull Map<String, JTree> trees) {
        createTreeAndSetupCommonActions(trees, IdeActions.GROUP_TYPE_HIERARCHY_POPUP);
    }

    @Nullable
    @Override
    protected PsiElement getElementFromDescriptor(
            @NotNull HierarchyNodeDescriptor hierarchyNodeDescriptor) {
        return hierarchyNodeDescriptor.getPsiElement();
    }

    @Override
    protected boolean isInterface(PsiElement psiElement) {
        return false;
//        return psiElement instanceof CeylonPsi.AnyInterfacePsi;
    }

    @Override
    protected boolean canBeDeleted(PsiElement psiElement) {
        return false;
    }

    @Override
    protected String getQualifiedName(PsiElement psiElement) {
        return ((CeylonPsi.DeclarationPsi) psiElement).getCeylonNode()
                .getDeclarationModel()
                .getQualifiedNameString();
    }

    @Nullable
    @Override
    protected JPanel createLegendPanel() {
        //OK!
        return null;
    }

    @Override
    protected boolean isApplicableElement(@NotNull PsiElement psiElement) {
        return psiElement instanceof CeylonPsi.ClassOrInterfacePsi;
    }

    @Nullable
    @Override
    protected Comparator<NodeDescriptor> getComparator() {
        if (HierarchyBrowserManager.getInstance(project)
                .getState().SORT_ALPHABETICALLY) {
            return AlphaComparator.INSTANCE;
        }
        else {
            //TODO: probably does not work!
            return SourceComparator.INSTANCE;
        }
    }

    private TypeHierarchyNodeDescriptor build(CeylonPsi.ClassOrInterfacePsi element) {
        Type extendedType =
                element.getCeylonNode()
                        .getDeclarationModel()
                        .getExtendedType();
        if (extendedType == null) {
            return new TypeHierarchyNodeDescriptor(element);
        }
        else {
            PsiElement psiElement
                    = CeylonReference.resolveDeclaration(extendedType.getDeclaration(), project);
            if (psiElement instanceof CeylonPsi.ClassOrInterfacePsi) {
                TypeHierarchyNodeDescriptor parentDescriptor =
                        build((CeylonPsi.ClassOrInterfacePsi) psiElement);
                TypeHierarchyNodeDescriptor nodeDescriptor =
                        new TypeHierarchyNodeDescriptor(parentDescriptor, element);
                parentDescriptor.children = new TypeHierarchyNodeDescriptor[] { nodeDescriptor };
                return nodeDescriptor;
            }
            else {
                return new TypeHierarchyNodeDescriptor(element);
            }
        }
    }

    private class TypeHierarchyNodeDescriptor extends HierarchyNodeDescriptor {
        private TypeHierarchyNodeDescriptor[] children;

        private TypeHierarchyNodeDescriptor(@NotNull CeylonPsi.TypeDeclarationPsi element) {
            super(project, null, element, true);
            myName = element.getCeylonNode().getIdentifier().getText();
        }
        private TypeHierarchyNodeDescriptor(@NotNull NodeDescriptor parentDescriptor,
                                           @NotNull CeylonPsi.TypeDeclarationPsi element) {
            super(project, parentDescriptor, element, false);
            myName = element.getCeylonNode().getIdentifier().getText();
        }

        private CeylonPsi.TypeDeclarationPsi getTypedDeclarationPsi() {
            return (CeylonPsi.TypeDeclarationPsi) super.getPsiElement();
        }

        @Override
        public boolean update() {
            boolean changes = super.update();
            final CompositeAppearance oldText = myHighlightedText;
            myHighlightedText = new CompositeAppearance();
            CeylonPsi.TypeDeclarationPsi psi = getTypedDeclarationPsi();
            String description =
                    "'" + descriptions_.get_().descriptionForPsi(psi, false) + "'";
            highlighter_.get_()
                    .highlightCompositeAppearance(myHighlightedText, description, project);
            Unit unit = psi.getCeylonNode().getUnit();
            if (unit!=null) {
                String qualifiedNameString =
                        unit.getPackage()
                            .getQualifiedNameString();
                myHighlightedText.getEnding()
                        .addText(" (" + qualifiedNameString + ")",
                            getPackageNameAttributes());
            }
            if (!Comparing.equal(myHighlightedText, oldText)) {
                changes = true;
            }
            return changes;
        }
    }

    private class SupertypesHierarchyTreeStructure extends HierarchyTreeStructure {
        private SupertypesHierarchyTreeStructure(CeylonPsi.ClassOrInterfacePsi element) {
            super(CeylonTypeHierarchyBrowser.this.project, new TypeHierarchyNodeDescriptor(element));
        }

        @NotNull
        @Override
        protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor parent) {
            TypeHierarchyNodeDescriptor descriptor =
                    (TypeHierarchyNodeDescriptor) parent;
            List<HierarchyNodeDescriptor> result = new ArrayList<HierarchyNodeDescriptor>();
            TypeDeclaration model =
                    descriptor.getTypedDeclarationPsi()
                            .getCeylonNode()
                            .getDeclarationModel();
            if (model!=null) {
                Type cl = model.getExtendedType();
                if (cl != null) {
                    PsiElement psiElement
                            = CeylonReference.resolveDeclaration(cl.getDeclaration(), project);
                    //TODO: what about Java types in the hierarchy!!!!
                    if (psiElement instanceof CeylonPsi.TypeDeclarationPsi) {
                        result.add(new TypeHierarchyNodeDescriptor(parent,
                                (CeylonPsi.TypeDeclarationPsi) psiElement));
                    }
                }
                for (Type type : model.getSatisfiedTypes()) {
                    PsiElement psiElement
                            = CeylonReference.resolveDeclaration(type.getDeclaration(), project);
                    //TODO: what about Java types in the hierarchy!!!!
                    if (psiElement instanceof CeylonPsi.TypeDeclarationPsi) {
                        result.add(new TypeHierarchyNodeDescriptor(parent,
                                (CeylonPsi.TypeDeclarationPsi) psiElement));
                    }
                }
            }
            return result.toArray(new HierarchyNodeDescriptor[0]);
        }
    }

    private class SubtypesHierarchyTreeStructure extends HierarchyTreeStructure {
        private final Set<PhasedUnit> modules;

        private SubtypesHierarchyTreeStructure(CeylonPsi.ClassOrInterfacePsi element) {
            super(project, new TypeHierarchyNodeDescriptor(element));
            modules = collectPhasedUnits(project);
        }

        @NotNull
        @Override
        protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor parent) {
            TypeHierarchyNodeDescriptor descriptor =
                    (TypeHierarchyNodeDescriptor) parent;
            if (descriptor.children!=null) {
                return descriptor.children;
            }
            List<TypeHierarchyNodeDescriptor> result = new ArrayList<>();
            TypeDeclaration model =
                    descriptor.getTypedDeclarationPsi()
                            .getCeylonNode()
                            .getDeclarationModel();
            if (model!=null) {
                for (PhasedUnit unit : modules) {
                    for (Declaration declaration : unit.getDeclarations()) {
                        if (declaration instanceof ClassOrInterface) {
                            ClassOrInterface ci = (ClassOrInterface) declaration;
                            Type extendedType = ci.getExtendedType();
                            if (extendedType != null) {
                                if (extendedType.getDeclaration().equals(model)) {
                                    PsiElement psiElement
                                            = CeylonReference.resolveDeclaration(ci, project);
                                    if (psiElement instanceof CeylonPsi.TypeDeclarationPsi) {
                                        result.add(new TypeHierarchyNodeDescriptor(descriptor,
                                                (CeylonPsi.TypeDeclarationPsi) psiElement));
                                    }
                                }
                            }
                            for (Type satisfiedType : ci.getSatisfiedTypes()) {
                                if (satisfiedType.getDeclaration().equals(model)) {
                                    PsiElement psiElement
                                            = CeylonReference.resolveDeclaration(ci, project);
                                    if (psiElement instanceof CeylonPsi.TypeDeclarationPsi) {
                                        result.add(new TypeHierarchyNodeDescriptor(descriptor,
                                                (CeylonPsi.TypeDeclarationPsi) psiElement));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            TypeHierarchyNodeDescriptor[] children
                    = result.toArray(new TypeHierarchyNodeDescriptor[0]);
            descriptor.children = children;
            return children;
        }
    }

    /*private TypeHierarchyNodeDescriptor root(TypeHierarchyNodeDescriptor child) {
        TypeHierarchyNodeDescriptor parentDescriptor =
                (TypeHierarchyNodeDescriptor)
                        child.getParentDescriptor();
        return parentDescriptor == null ? child : root(parentDescriptor);
    }*/

    private class TypeHierarchyTreeStructure extends HierarchyTreeStructure {
        private final Set<PhasedUnit> modules;

        private TypeHierarchyTreeStructure(CeylonPsi.ClassOrInterfacePsi element) {
            super(project, build(element));
            modules = collectPhasedUnits(project);
            setBaseElement(myBaseDescriptor);
        }

        @NotNull
        @Override
        protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor parent) {
            TypeHierarchyNodeDescriptor descriptor =
                    (TypeHierarchyNodeDescriptor) parent;
            if (descriptor.children!=null) {
                return descriptor.children;
            }
            List<TypeHierarchyNodeDescriptor> result = new ArrayList<>();
            TypeDeclaration model =
                    descriptor.getTypedDeclarationPsi()
                            .getCeylonNode()
                            .getDeclarationModel();
            if (model!=null) {
                for (PhasedUnit unit : modules) {
                    for (Declaration declaration : unit.getDeclarations()) {
                        if (declaration instanceof ClassOrInterface) {
                            ClassOrInterface ci = (ClassOrInterface) declaration;
                            Type extendedType = ci.getExtendedType();
                            if (extendedType != null) {
                                if (extendedType.getDeclaration().equals(model)) {
                                    PsiElement psiElement
                                            = CeylonReference.resolveDeclaration(ci, project);
                                    if (psiElement instanceof CeylonPsi.TypeDeclarationPsi) {
                                        result.add(new TypeHierarchyNodeDescriptor(descriptor,
                                                (CeylonPsi.TypeDeclarationPsi) psiElement));
                                    }
                                }
                            }
                            for (Type satisfiedType : ci.getSatisfiedTypes()) {
                                if (satisfiedType.getDeclaration().equals(model)) {
                                    PsiElement psiElement
                                            = CeylonReference.resolveDeclaration(ci, project);
                                    if (psiElement instanceof CeylonPsi.TypeDeclarationPsi) {
                                        result.add(new TypeHierarchyNodeDescriptor(descriptor,
                                                (CeylonPsi.TypeDeclarationPsi) psiElement));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            TypeHierarchyNodeDescriptor[] children
                    = result.toArray(new TypeHierarchyNodeDescriptor[0]);
            descriptor.children = children;
            return children;
        }
    }

}
