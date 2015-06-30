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
import com.redhat.ceylon.ide.common.model.CeylonProject;
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
public class CeylonFacetConfiguration implements FacetConfiguration, PersistentStateComponent<CeylonFacetState> {

    public static final String COMPILATION_TAB = "Compilation";
    public static final String REPOS_TAB = "Repositories";

    private CeylonProject<Module> ceylonProject;
    private CeylonFacetState state;

    @Override
    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        return new FacetEditorTab[]{
                new CeylonFacetTab(COMPILATION_TAB, new PageOne()),
                new CeylonFacetTab(REPOS_TAB, new PageTwo())
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
    public CeylonFacetState getState() {
        ceylonProject.getConfiguration().save();
        return state;
    }

    @Override
    public void loadState(CeylonFacetState state) {
        System.out.println("loadState()"); // When is it called anyway?
        this.state = state;
    }

    public void setModule(Module module) {
        IdeaCeylonProjects ceylonModel = module.getProject().getComponent(IdeaCeylonProjects.class);
        ceylonProject = ceylonModel.getProject(module);

        if (ceylonProject == null && ceylonModel.addProject(module)) {
            ceylonProject = ceylonModel.getProject(module);
        }

        if (state == null) {
            state = new CeylonFacetState();
            System.out.println("!!!! Instantiating CeylonFacetState !!!!");
        }
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
            return form.isModified(ceylonProject, state);
        }

        @Override
        public void reset() {
            form.load(ceylonProject, state);
        }

        @Override
        public void apply() throws ConfigurationException {
            form.apply(ceylonProject, state);
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
