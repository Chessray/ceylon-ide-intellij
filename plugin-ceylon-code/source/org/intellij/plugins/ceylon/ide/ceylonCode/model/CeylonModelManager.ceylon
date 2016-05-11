import ceylon.interop.java {
    JavaRunnable
}

import com.intellij.codeInsight.daemon {
    DaemonCodeAnalyzer
}
import com.intellij.openapi.application {
    ApplicationManager {
        application
    },
    ModalityState
}
import com.intellij.openapi.components {
    ProjectComponent
}
import com.intellij.openapi.fileEditor {
    FileEditorManager,
    FileDocumentManager {
        fileDocumentManager=instance
    },
    FileEditorManagerEvent,
    FileEditorManagerListener
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.progress {
    ProgressManager {
        progressManager=instance
    },
    Task {
        Backgroundable
    },
    ProgressIndicator,
    PerformInBackgroundOption
}
import com.intellij.openapi.roots {
    ProjectRootManager {
        projectRootManager=getInstance
    }
}
import com.intellij.openapi.startup {
    StartupManager {
        startupManager=getInstance
    }
}
import com.intellij.openapi.vfs {
    VirtualFileListener,
    VirtualFile,
    VirtualFilePropertyEvent,
    VirtualFileEvent,
    VirtualFileMoveEvent,
    VirtualFileCopyEvent,
    VirtualFileManager
}
import com.intellij.testFramework {
    LightVirtualFile
}
import com.intellij.util.concurrency {
    QueueProcessor,
    AppExecutorUtil
}
import com.redhat.ceylon.ide.common.model {
    ChangeAware,
    ModelAliases,
    ModelListenerAdapter
}

import java.util {
    Timer
}
import java.util.concurrent {
    TimeUnit
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model.parsing {
    ProgressIndicatorMonitor
}

shared class CeylonModelManager(model) 
    satisfies ProjectComponent
    & VirtualFileListener
    & FileEditorManagerListener
    & ModelListenerAdapter<Module, VirtualFile, VirtualFile, VirtualFile>
    & ChangeAware<Module, VirtualFile, VirtualFile, VirtualFile>
    & ModelAliases<Module, VirtualFile, VirtualFile, VirtualFile> {
    shared IdeaCeylonProjects model;
    shared variable Integer typecheckingPeriod = 5;
    shared variable Boolean periodicTypecheckingEnabled = true;
    variable value ideaProjectReady = false;
    value queueProcessor = QueueProcessor.createRunnableQueueProcessor();
    
    componentName => "CeylonModelManager";
    
    shared void startBuild() {
        void reschedule() {
            if (periodicTypecheckingEnabled,
                ! queueProcessor.hasPendingItemsToProcess()) {
                AppExecutorUtil.appScheduledExecutorService.schedule(JavaRunnable(startBuild), typecheckingPeriod, TimeUnit.\iSECONDS);
            }
        }
        if (ideaProjectReady) {
            queueProcessor.add(JavaRunnable { 
                void run () {
                    if (model.ceylonProjects.any((ceylonProject) 
                        => ceylonProject.build.somethingToDo)) {
                        application.invokeAndWait(JavaRunnable {
                            run() => progressManager.run(object extends Backgroundable(
                                model.ideaProject, 
                                "ceylon model update", 
                                true, 
                                PerformInBackgroundOption.\iALWAYS_BACKGROUND) {
                                
                                shared actual void run(ProgressIndicator progressIndicator) {
                                    value monitor = ProgressIndicatorMonitor.wrap(progressIndicator);
                                    value ticks = model.ceylonProjectNumber * 1000;
                                    try (progress = monitor.Progress(ticks, "Updating Ceylon Model")) {
                                        for (ceylonProject in model.ceylonProjectsInTopologicalOrder) {
                                            ceylonProject.build.performBuild(progress.newChild(1000));
                                        }
                                    }
                                    application.invokeAndWait(JavaRunnable(() =>
                                        DaemonCodeAnalyzer.getInstance(model.ideaProject).restart()), ModalityState.\iNON_MODAL);
                                }
                                
                                shared actual void onFinished() {
                                    queueProcessor.dismissLastTasks(1);
                                    reschedule();
                                }
                            });
                        }, ModalityState.any());
                    }
                }
            });
        } else {
            reschedule();
        }
    }
    
    /***************************************************************************
      ModelListenerAdapter implementations
     ***************************************************************************/
    
    ceylonProjectAdded(CeylonProjectAlias ceylonProject) =>
            startBuild();
    
    /***************************************************************************
      ProjectComponent implementations
     ***************************************************************************/
    
    shared actual void disposeComponent() {
        VirtualFileManager.instance.removeVirtualFileListener(this);
        model.removeModelListener(this);
    }
    
    shared actual void initComponent() {
        VirtualFileManager.instance.addVirtualFileListener(this);
        model.ideaProject.messageBus.connect()
                .subscribe(FileEditorManagerListener.\iFILE_EDITOR_MANAGER, this);
        model.addModelListener(this);
    }

    shared actual void projectOpened() => 
            startupManager(model.ideaProject)
                .runWhenProjectIsInitialized(JavaRunnable(() {
                    ideaProjectReady = true;
                    startBuild();
                }));

    shared actual void projectClosed() {
        ideaProjectReady = false;
    }

    /***************************************************************************
      VirtualFileListener implementation that notifies the file changes
      to the Ceylon Model
     ***************************************************************************/

    beforeContentsChange(VirtualFileEvent evt) => noop();
    beforeFileDeletion(VirtualFileEvent evt) => noop();
    beforeFileMovement(VirtualFileMoveEvent evt) => noop();
    beforePropertyChange(VirtualFilePropertyEvent evt) => noop();
    
    shared actual void contentsChanged(VirtualFileEvent evt) {
        value file = evt.file;
        if (! file.directory) {
            model.fileTreeChanged({
                NativeFileContentChange(file)
            });
        }
    }
        
    fileCopied(VirtualFileCopyEvent evt) => fileCreated(evt);
    
    shared actual void fileCreated(VirtualFileEvent evt) {
        value file = evt.file;
        model.fileTreeChanged({
            if (file.directory)
            then NativeFolderAddition(file)
            else NativeFileAddition(file)
        });
    }
    
    shared actual void fileDeleted(VirtualFileEvent evt) {
        value file = evt.file;
        model.fileTreeChanged({
            if (file.directory)
            then NativeFolderRemoval(file, null)
            else NativeFileRemoval(file, null)
        });
    }
    
    shared actual void fileMoved(VirtualFileMoveEvent evt) {
        value file = evt.file;
        value oldParent = evt.oldParent;
        value oldFile = object extends LightVirtualFile(file.name) {
            parent => oldParent;
            directory => file.directory;
        };
        
        model.fileTreeChanged(
            if (file.directory)
            then { NativeFolderRemoval(file, oldFile), NativeFolderAddition(file) }
            else { NativeFileRemoval(file, oldFile), NativeFileAddition(file) }
        );
    }

    shared actual void propertyChanged(VirtualFilePropertyEvent evt) {
        // TODO Manage the file rename !
    }

    /***************************************************************************
      FileEditorManagerListener implementation that:
       - forces to save the document when leaving an editor
       - triggers the typechecking when switching editors
     ***************************************************************************/
    
    fileClosed(FileEditorManager manager, VirtualFile file) => save(file);

    fileOpened(FileEditorManager manager, VirtualFile file) => noop();
    
    shared actual void selectionChanged(FileEditorManagerEvent evt) {
        if (exists oldFile = evt.oldFile,
            exists newFile = evt.newFile) {
            save(oldFile);
            // TODO: if the fileChange event is already triggered and managed (=> synchronous) then
            // we could schedule a Build. Knowing that scheduling it again does nothing it the first one is not started.
            startBuild();
        }
    }
    
    void save(VirtualFile file) {
        assert(!file.directory);
        if (exists ceylonProject = ceylonProjectForFile(file),
            ceylonProject.isCeylon(file),
            ceylonProject.isFileInSourceFolder(file),
            exists doc = fileDocumentManager.getCachedDocument(file)) {
            
            // Will trigger contentsChanged(), which will call the typechecker
            fileDocumentManager.saveDocument(doc);
        }
    }

    /***************************************************************************
      Utility functions
     ***************************************************************************/
    
    CeylonProjectAlias? ceylonProjectForFile(VirtualFile? file) =>
            if (exists file) 
            then model.getProject(projectRootManager(model.ideaProject).fileIndex.getModuleForFile(file))
            else null;
}
