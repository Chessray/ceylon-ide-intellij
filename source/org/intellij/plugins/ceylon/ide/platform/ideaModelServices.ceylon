import com.intellij.openapi.\imodule {
    Module,
    ModuleManager
}
import com.intellij.openapi.roots {
    ModuleRootManager {
        moduleRootManager=getInstance
    }
}
import com.intellij.openapi.vfs {
    VirtualFile,
    VfsUtilCore,
    VirtualFileVisitor,
    VfsUtil
}
import com.redhat.ceylon.ide.common.model {
    EditedSourceFile,
    ProjectSourceFile,
    CrossProjectSourceFile
}
import com.redhat.ceylon.ide.common.model.parsing {
    RootFolderScanner
}
import com.redhat.ceylon.ide.common.platform {
    ModelServices
}
import com.redhat.ceylon.ide.common.typechecker {
    EditedPhasedUnit,
    ProjectPhasedUnit,
    CrossProjectPhasedUnit
}

import org.intellij.plugins.ceylon.ide.model {
    concurrencyManager,
    IdeaCeylonProject
}
import org.jetbrains.jps.model.java {
    JavaResourceRootType
}

shared object ideaModelServices satisfies ModelServices<Module, VirtualFile, VirtualFile,VirtualFile> {

    newCrossProjectSourceFile(CrossProjectPhasedUnit<Module,VirtualFile,VirtualFile,VirtualFile> phasedUnit)
            => CrossProjectSourceFile(phasedUnit);
    
    newEditedSourceFile(EditedPhasedUnit<Module,VirtualFile,VirtualFile,VirtualFile> phasedUnit)
            => EditedSourceFile(phasedUnit);
    
    newProjectSourceFile(ProjectPhasedUnit<Module,VirtualFile,VirtualFile,VirtualFile> phasedUnit)
            => ProjectSourceFile(phasedUnit);

    // TODO : review this to use : ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(virtualFile);
    shared actual Boolean isResourceContainedInProject(VirtualFile resource, CeylonProjectAlias ceylonProject)
            => concurrencyManager.needReadAccess(() {
                for (root in moduleRootManager(ceylonProject.ideArtifact).contentRoots) {
                    if (VfsUtil.isAncestor(root, resource, true)) {
                        return true;
                    }
                }
                else {
                    return false;
                }
            });


    // TODO check if the module is open?
    nativeProjectIsAccessible(Module nativeProject) => true;

    referencedNativeProjects(Module mod)
            => concurrencyManager.needReadAccess(()
                => { *moduleRootManager(mod).dependencies} );

    referencingNativeProjects(Module mod)
            => { *concurrencyManager.needReadAccess(()
                    => ModuleManager.getInstance(mod.project)
                        .getModuleDependentModules(mod)) };

    shared actual {VirtualFile*} resourceNativeFolders(CeylonProjectAlias ceylonProject) {
        value roots
                = concurrencyManager.needReadAccess(()
                    => moduleRootManager(ceylonProject.ideArtifact)
                        ?.getSourceRoots(JavaResourceRootType.resource));
        return { if (exists roots) for (root in roots) root };
    }

    scanRootFolder(RootFolderScanner<Module,VirtualFile,VirtualFile,VirtualFile> scanner)
        => VfsUtilCore.visitChildrenRecursively(scanner.nativeRootDir,
            object extends VirtualFileVisitor<Nothing>() {
                visitFile(VirtualFile file)
                        => scanner.visitNativeResource(file);
            }
        );

    function sourceNativeFolder(IdeaCeylonProject ceylonProject, String dir) {
        try {
            value path = dir.removeInitial("./").split('/'.equals);
            return VfsUtil.findRelativeFile(ceylonProject.moduleRoot, *path);
        }
        catch (Exception e) {
            return null;
        }
    }

    shared actual {VirtualFile*} sourceNativeFolders(CeylonProjectAlias ceylonProject) {
        assert (is IdeaCeylonProject ceylonProject);

        return {
            for (dir in ceylonProject.configuration.sourceDirectories)
            if (exists vfile = sourceNativeFolder(ceylonProject, dir))
            vfile
        };
    }
}
