package org.intellij.plugins.ceylon.ide.structureView;

import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.ideaIcons_;

import javax.swing.*;

class CeylonTypeAliasTreeElement extends CeylonDeclarationTreeElement<CeylonPsi.TypeAliasDeclarationPsi> {

    CeylonTypeAliasTreeElement(CeylonPsi.TypeAliasDeclarationPsi psiElement) {
        super(psiElement, false);
    }

    @Override
    public Icon getIcon(boolean open) {
        return ideaIcons_.get_().forDeclaration(getElement().getCeylonNode());
    }
}
