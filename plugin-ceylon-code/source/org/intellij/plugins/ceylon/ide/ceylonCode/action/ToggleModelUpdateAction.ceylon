import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent,
    PlatformDataKeys
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    getModelManager
}

shared class ToggleModelUpdateAction() extends AnAction() {

    shared actual void actionPerformed(AnActionEvent e) {
        if (exists project = PlatformDataKeys.project.getData(e.dataContext),
            exists modelManager = getModelManager(project)) {
            modelManager.automaticModelUpdateEnabled
                    = !modelManager.automaticModelUpdateEnabled;
        }
    }

    shared actual void update(AnActionEvent e) {
        if (exists project = PlatformDataKeys.project.getData(e.dataContext),
            exists modelManager = getModelManager(project)) {

            e.presentation.enabled = true;

            value what = " automatic update of the Ceylon model (``modelManager.delayBeforeUpdatingAfterChange/1000`` seconds after any change)";
            value action = modelManager.automaticModelUpdateEnabled then "Disable" else "Enable";
            e.presentation.text = action + what;
        } else {
            e.presentation.enabled = false;
        }
    }
}
