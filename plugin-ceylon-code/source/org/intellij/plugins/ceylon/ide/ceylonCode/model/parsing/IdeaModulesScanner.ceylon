import com.redhat.ceylon.ide.common.model.parsing {
    ModulesScanner
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProject
}
import com.redhat.ceylon.ide.common.util {
    ProgressMonitor
}
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs {
    IdeaVirtualFolder
}

shared class IdeaModulesScanner(IdeaCeylonProject project, VirtualFile srcDir)
        extends ModulesScanner<Module,VirtualFile,VirtualFile,VirtualFile>(
    project, IdeaVirtualFolder(srcDir, project), DummyProgressMonitor()
) {

}

shared class DummyProgressMonitor() extends ProgressMonitor<String>("") {
    shared actual void subTask(String? desc) {}
    
    shared actual void worked(Integer amount) {}
    shared actual Boolean cancelled => false;
}
