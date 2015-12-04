package org.intellij.plugins.ceylon.ide.project;

import ceylon.language.Boolean;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.PlatformIcons;
import com.redhat.ceylon.common.config.CeylonConfigFinder;
import com.redhat.ceylon.common.config.Repositories;
import com.redhat.ceylon.ide.common.configuration.CeylonRepositoryConfigurator;
import org.apache.commons.lang.StringUtils;
import org.intellij.plugins.ceylon.ide.CeylonBundle;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PageTwo extends CeylonRepositoryConfigurator implements CeylonConfigForm {
    private CollectionListModel<String> listModel = new CollectionListModel<>();
    private JPanel panel;
    private TextFieldWithBrowseButton systemRepository;
    private TextFieldWithBrowseButton outputDirectory;
    private JCheckBox flatClasspath;
    private JCheckBox exportMavenDeps;
    private JButton addExternalRepo;
    private JButton addMavenRepo;
    private JButton removeRepo;
    private JButton upButton;
    private JButton downButton;
    private JButton addRepo;
    private JButton addRemoteRepo;
    private JList<String> repoList;
    private Module module;

    public PageTwo() {
        Repositories repositories = Repositories.withConfig(CeylonConfigFinder.loadDefaultConfig(null));

        outputDirectory.setText(
                repositories
                        .getOutputRepository()
                        .getUrl()
        );

        // TODO if we knew the module's location at this time, we could get this from the CeylonConfig
        addGlobalLookupRepo(repositories.getRepository("USER").getUrl());
        addOtherRemoteRepo(repositories.getRepository("REMOTE").getUrl());

        listModel.add(repositories.getRepository("USER").getUrl());
        listModel.add(repositories.getRepository("REMOTE").getUrl());

        repoList.setModel(listModel);
        repoList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component cmp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (isFixedRepoIndex(index)) {
                    setIcon(PlatformIcons.LIBRARY_ICON);

                    if (isFixedRepoIndex(index)) {
                        setForeground(JBColor.GRAY);
                    }
                }

                return cmp;
            }
        });

        systemRepository.addBrowseFolderListener(CeylonBundle.message("ceylon.facet.settings.selectsystemrepo"), null, null, FileChooserDescriptorFactory.createSingleFolderDescriptor());
        outputDirectory.addBrowseFolderListener(new BrowseOutputDirectoryListener());
        repoList.addListSelectionListener(new RepoListSelectionListener());
        addRemoteRepo.addActionListener(new AddRemoteRepoListener());
        removeRepo.addActionListener(new RemoveRepoListener());
        upButton.addActionListener(new MoveUpListener());
        downButton.addActionListener(new MoveDownListener());
        addExternalRepo.addActionListener(new AddExternalRepoListener());
        addMavenRepo.addActionListener(new AddMavenRepoListener());

        JTextField textField = systemRepository.getTextField();
        if (textField instanceof JBTextField) {
            ((JBTextField) textField).getEmptyText().setText("Use IDE system modules");
        }
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    public String getOutputDirectory() {
        return outputDirectory.getText();
    }

    @Override
    public void apply(IdeaCeylonProject config) {
        config.getIdeConfiguration().setSystemRepository(ceylon.language.String.instance(systemRepository.getText()));
        config.getConfiguration().setOutputRepo(outputDirectory.getText());
        config.getConfiguration().setProjectFlatClasspath(Boolean.instance(flatClasspath.isSelected()));
        config.getConfiguration().setProjectAutoExportMavenDependencies(Boolean.instance(exportMavenDeps.isSelected()));
        applyToConfiguration(config.getConfiguration());
    }

    @Override
    public boolean isModified(IdeaCeylonProject config) {
        ceylon.language.String systemRepo = config.getIdeConfiguration().getSystemRepository();
        return !StringUtils.equals(systemRepo == null ? null : systemRepo.toString(), systemRepository.getText())
                || !StringUtils.equals(config.getConfiguration().getOutputRepo(), outputDirectory.getText())
                || !Boolean.equals(flatClasspath.isSelected(), config.getConfiguration().getProjectFlatClasspath())
                || !Boolean.equals(exportMavenDeps.isSelected(), config.getConfiguration().getProjectAutoExportMavenDependencies())
                || isRepoConfigurationModified(config.getConfiguration())
                ;
    }

    @Override
    public void load(IdeaCeylonProject config) {
        module = config.getIdeArtifact();
        ceylon.language.String systemRepository = config.getIdeConfiguration().getSystemRepository();
        this.systemRepository.setText(systemRepository == null ? null : systemRepository.toString());
        outputDirectory.setText(config.getConfiguration().getOutputRepo());
        flatClasspath.setSelected(boolValue(config.getConfiguration().getProjectFlatClasspath()));
        exportMavenDeps.setSelected(boolValue(config.getConfiguration().getProjectAutoExportMavenDependencies()));

        listModel.removeAll();
        loadFromConfiguration(config.getConfiguration());
    }

    private boolean boolValue(Boolean bool) {
        //noinspection SimplifiableConditionalExpression
        return bool == null ? false : bool.booleanValue();
    }

    @Override
    public int[] getSelection() {
        return repoList.getSelectedIndices();
    }

    @Override
    public Object enableRemoveButton(boolean enabled) {
        removeRepo.setEnabled(enabled);
        return null;
    }

    @Override
    public Object enableUpButton(boolean enabled) {
        upButton.setEnabled(enabled);
        return null;
    }

    @Override
    public Object enableDownButton(boolean enabled) {
        downButton.setEnabled(enabled);
        return null;
    }

    @Override
    public String removeRepositoryFromList(long index) {
        String repo = listModel.getElementAt((int) index);
        listModel.remove((int) index);
        return repo;
    }

    @Override
    public Object addRepositoryToList(long index, String repo) {
        listModel.add((int) index, repo);
        repoList.setSelectedIndex((int) index);
        return null;
    }

    @Override
    public Object addAllRepositoriesToList(String[] repos) {
        for (String repo : repos) {
            listModel.add(repo);
        }
        return null;
    }

    private class BrowseOutputDirectoryListener extends TextBrowseFolderListener {

        public BrowseOutputDirectoryListener() {
            super(FileChooserDescriptorFactory.createSingleFolderDescriptor());
        }

        @Nullable
        @Override
        protected Project getProject() {
            return module == null ? null : module.getProject();
        }

        @Nullable
        @Override
        protected VirtualFile getInitialFile() {
            Project project = getProject();
            if (project != null) {
                return project.getBaseDir();
            }
            return super.getInitialFile();
        }

        @NotNull
        @Override
        protected String chosenFileToResultingText(@NotNull VirtualFile file) {
            Project project = getProject();

            if (project != null && VfsUtil.isAncestor(project.getBaseDir(), file, true)) {
                return "./" + VfsUtil.getRelativePath(file, project.getBaseDir());
            }

            return super.chosenFileToResultingText(file);
        }
    }

    private class AddExternalRepoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            VirtualFile file = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null);
            if (file != null) {
                addExternalRepo(file.getPresentableUrl());
            }
        }
    }

    private class AddRemoteRepoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String uri = JOptionPane.showInputDialog(PageTwo.this.panel, CeylonBundle.message("project.wizard.repo.uri.description"), CeylonBundle.message("project.wizard.repo.uri.title"), JOptionPane.QUESTION_MESSAGE);

            if (StringUtils.isNotBlank(uri)) {
                addRemoteRepo(uri);
            }
        }
    }

    private class AddMavenRepoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            final AetherRepositoryDialog dialog = new AetherRepositoryDialog(panel);
            dialog.pack();

            if (dialog.showAndGet()) {
                String repo = dialog.getRepository();

                if (repo != null) {
                    addAetherRepo(repo);
                }
            }
        }
    }

    private class RepoListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            updateButtonState();
        }
    }

    private class RemoveRepoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            removeSelectedRepo();
            repoList.clearSelection();
        }
    }

    private class MoveUpListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!repoList.isSelectionEmpty()) {
                moveSelectedReposUp();
            }
        }
    }

    private class MoveDownListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!repoList.isSelectionEmpty()) {
                moveSelectedReposDown();
            }
        }
    }
}
