package org.intellij.plugins.ceylon.ide.runner;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.RawCommandLineEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Graphical editor for Ceylon run configurations.
 */
public class CeylonRunConfigurationEditor extends SettingsEditor<CeylonRunConfiguration> {
    private JTextField myRunnableName;
    private JTextField myCeylonModule;
    private JPanel myPanel;
    private RawCommandLineEditor myArguments;
    private RawCommandLineEditor myVmOptions;
    private ModulesComboBox myIdeModule;

    public CeylonRunConfigurationEditor(Project project) {
//        myFileName.addBrowseFolderListener("Choose .ceylon file", "Please choose the path to the Ceylon file to run",
//                project, FileChooserDescriptorFactory.createSingleLocalFileDescriptor());
//        myPackage = new PackageNameReferenceEditorCombo(null, project, null, RefactoringBundle.message("choose.destination.package"));
    }

    @Override
    protected void resetEditorFrom(CeylonRunConfiguration config) {
        myRunnableName.setText(config.getTopLevelNameFull());
        myCeylonModule.setText(config.getCeylonModule());
        myArguments.setText(config.getArguments());
        myVmOptions.setText(config.getVmOptions());
        myIdeModule.setModules(config.getValidModules());
        myIdeModule.setSelectedModule(config.getConfigurationModule().getModule());
    }

    @Override
    protected void applyEditorTo(CeylonRunConfiguration s) throws ConfigurationException {
        s.setTopLevelNameFull(myRunnableName.getText());
        s.setCeylonModule(myCeylonModule.getText());
        s.setArguments(myArguments.getText());
        s.setVmOptions(myVmOptions.getText());
        s.getConfigurationModule().setModule(myIdeModule.getSelectedModule());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myPanel;
    }

    private void createUIComponents() {
    }

}
