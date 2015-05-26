package org.intellij.plugins.ceylon.ide.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.redhat.ceylon.common.config.CeylonConfig;
import com.redhat.ceylon.ide.common.CeylonProject;
import com.redhat.ceylon.ide.common.CeylonProjectConfig;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.project.CeylonConfigForm;
import org.intellij.plugins.ceylon.ide.project.PageOne;
import org.intellij.plugins.ceylon.ide.project.PageTwo;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Settings for the Ceylon facet. Uses the same component than the "new module" wizard.
 */
public class CeylonFacetConfiguration implements FacetConfiguration, PersistentStateComponent<CeylonConfig> {

    private CeylonConfig config;

    @Override
    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        return new FacetEditorTab[]{
                new CeylonFacetTab("Compilation", new PageOne()),
                new CeylonFacetTab("Paths", new PageTwo())
        };
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        // goto loadState()
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        // goto getState()
    }

    @Nullable
    @Override
    public CeylonConfig getState() {
        // We don't need IntelliJ to persist anything, we'll do it by ourselves
        // FIXME

        return new CeylonConfig();
    }

    @Override
    public void loadState(CeylonConfig state) {
        System.out.println("loadState()"); // When is it called anyway?
    }

    public void setModule(Module module) {
        IdeaCeylonProjects ceylonModel = module.getProject().getComponent(IdeaCeylonProjects.class);
        CeylonProject<Module> ceylonProject = ceylonModel.getProject(module);
        CeylonProjectConfig<Module> ceylonConfig = ceylonProject.getConfiguration();

        // TODO Use CeylonConfig or CeylonProjectConfig?
        config = ceylonConfig.getProjectConfig();
    }

    private class CeylonFacetTab extends FacetEditorTab {

        private String tabName;
        private CeylonConfigForm form;

        private CeylonFacetTab(String tabName, CeylonConfigForm form) {
            this.tabName = tabName;
            this.form = form;
        }

        @NotNull
        @Override
        public JComponent createComponent() {
            return form.getPanel();
        }

        @Override
        public boolean isModified() {
            return form.isModified(CeylonFacetConfiguration.this.config);
        }

        @Override
        public void reset() {
            form.loadCeylonConfig(config);
        }

        @Override
        public void apply() throws ConfigurationException {
            form.updateCeylonConfig(CeylonFacetConfiguration.this.config);
        }

        @Override
        public void disposeUIResources() {
        }

        @Nls
        @Override
        public String getDisplayName() {
            return tabName;
        }
    }
}
