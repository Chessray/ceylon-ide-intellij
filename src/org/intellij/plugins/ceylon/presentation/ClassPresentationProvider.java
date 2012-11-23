package org.intellij.plugins.ceylon.presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.util.PlatformIcons;
import org.intellij.plugins.ceylon.psi.CeylonTypeNameDeclaration;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ClassPresentationProvider implements ItemPresentationProvider<CeylonTypeNameDeclaration> {
    @Override
    public ItemPresentation getPresentation(final CeylonTypeNameDeclaration item) {

        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return item.getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return "(" + item.getContainingFile().getName() + ")";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return PlatformIcons.CLASS_ICON;
            }
        };
    }
}
