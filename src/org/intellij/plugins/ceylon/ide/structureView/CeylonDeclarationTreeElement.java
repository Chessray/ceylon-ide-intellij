package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.LocationPresentation;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypeElement;
import com.intellij.util.ui.UIUtil;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.*;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.ceylonDeclarationDescriptionProvider_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

import static com.redhat.ceylon.ide.common.util.toJavaString_.toJavaString;

abstract class CeylonDeclarationTreeElement<Decl extends CeylonPsi.DeclarationPsi>
        extends PsiTreeElementBase<Decl>
        implements ColoredItemPresentation, LocationPresentation, SortableTreeElement {

    private boolean isInherited;

    private ceylonDeclarationDescriptionProvider_ provider =
            ceylonDeclarationDescriptionProvider_.get_();

    CeylonDeclarationTreeElement(Decl psiElement, boolean isInherited) {
        super(psiElement);
        this.isInherited = isInherited;
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        CeylonDeclarationTreeElement that =
                (CeylonDeclarationTreeElement) o;
        return isInherited == that.isInherited;
    }

    @Override
    public String toString() {
        return getPresentableText();
    }

    @Nullable
    @Override
    public TextAttributesKey getTextAttributesKey() {
        return isInherited ? CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES : null;
    }

    @Override
    public String getLocationString() {
        Declaration model =
                getElement()
                        .getCeylonNode()
                        .getDeclarationModel();
        if (model != null) {
            if (isInherited
                    && model.isClassOrInterfaceMember()) {
                ClassOrInterface container =
                        (ClassOrInterface)
                                model.getContainer();
                return " " + UIUtil.rightArrow() + container.getName();
            }
            Declaration refined =
                    model.getRefinedDeclaration();
            if (refined !=null &&
                !refined.equals(model)) {
                ClassOrInterface container =
                        (ClassOrInterface)
                                refined.getContainer();
                return " " + UIUtil.upArrow("^") + container.getName();
            }
        }
        return super.getLocationString();
    }

    @Override
    public String getLocationSuffix() {
        return "";
    }

    @Override
    public String getLocationPrefix() {
        return " ";
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return toJavaString(provider.getDescription(getElement(), false, false));
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        Tree.Identifier id = getElement().getCeylonNode().getIdentifier();
        return id==null ? "" : id.getText();
    }
}
