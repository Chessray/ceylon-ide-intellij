import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent,
    PlatformDataKeys
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    getCeylonProjects,
    getModelManager
}

shared class ResetModelAction() extends AnAction() {

    shared actual void actionPerformed(AnActionEvent e) {
        if (exists project = PlatformDataKeys.project.getData(e.dataContext),
            exists projects = getCeylonProjects(project)) {

            for (p in projects.ceylonProjects) {
                p.build.requestFullBuild();
                p.build.classPathChanged();
            }

            if (exists man = getModelManager(project)) {
                man.scheduleModelUpdate(0, true);
            }
        }
    }

    shared actual void update(AnActionEvent e) {
        if (exists project = PlatformDataKeys.project.getData(e.dataContext),
            exists projects = getCeylonProjects(project),
            !projects.ceylonProjects.empty) {

            e.presentation.enabled = true;
        } else {
            e.presentation.enabled = false;
        }
    }
}
