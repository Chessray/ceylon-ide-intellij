package org.intellij.plugins.ceylon.ide.action;

import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import org.jetbrains.annotations.Nullable;

import java.util.Properties;

import static com.redhat.ceylon.ide.validate.NameValidator.packageNameIsLegal;
import static org.intellij.plugins.ceylon.ide.CeylonBundle.message;

public class CeylonAddPackageAction extends CeylonAddingFilesAction {
    public CeylonAddPackageAction() {
        super(IconLoader.getIcon("/icons/package.png"));
    }

    @Override
    protected void createFiles(final AnActionEvent e, final TypeChecker typeChecker, final VirtualFile srcRoot, final String eventPackage, final PsiDirectory eventPsiDir) {
        final String packageName = Messages.showInputDialog(e.getProject(), message("ceylon.package.wizard.message"), message("ceylon.package.wizard.title"), null, eventPackage, new AddPackageInputValidator());

        if (packageName != null) {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    FileTemplateManager templateManager = FileTemplateManager.getInstance(e.getProject());

                    PsiDirectory srcRootDirectory = PsiManager.getInstance(e.getProject()).findDirectory(srcRoot);
                    PsiDirectory subdirectory = DirectoryUtil.createSubdirectories(packageName, srcRootDirectory, ".");

                    Properties variables = new Properties();
                    variables.put("MODULE_NAME", packageName);

                    try {
                        FileTemplateUtil.createFromTemplate(templateManager.getInternalTemplate("package.ceylon"), "package.ceylon", variables, subdirectory);
                    } catch (Exception e1) {
                        Logger.getInstance(CeylonAddModuleAction.class).error("Can't create file from template", e1);
                    }
                }
            });
        }
    }

    private class AddPackageInputValidator implements InputValidatorEx {

        @Override
        public boolean checkInput(String name) {
            return !name.trim().isEmpty() && packageNameIsLegal(name);
        }

        @Override
        public boolean canClose(String inputString) {
            return checkInput(inputString);
        }

        @Nullable
        @Override
        public String getErrorText(String name) {
            if (name.trim().isEmpty()) {
                return message("ceylon.package.wizard.error.blank");
            } else if (!packageNameIsLegal(name)) {
                return message("ceylon.package.wizard.error.illegal");
            }
            return null;
        }
    }
}
