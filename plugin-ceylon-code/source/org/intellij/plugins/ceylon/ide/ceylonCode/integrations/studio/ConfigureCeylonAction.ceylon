import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent,
    LangDataKeys
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

shared class ConfigureCeylonAction() extends AnAction(ideaIcons.ceylon) {
    shared actual void actionPerformed(AnActionEvent e) {
        if (exists mod = e.getData(LangDataKeys.\iMODULE_CONTEXT)) {
            AndroidStudioSupportImpl().setupModule(mod);
        }
    }

    shared actual void update(AnActionEvent e) {
        if (exists mod = e.getData(LangDataKeys.\iMODULE_CONTEXT)) {
            e.presentation.enabledAndVisible = true;
        } else {
            e.presentation.enabledAndVisible = false;
        }
    }
}
