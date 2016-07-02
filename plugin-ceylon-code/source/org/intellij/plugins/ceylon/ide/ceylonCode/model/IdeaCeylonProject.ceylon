import ceylon.interop.java {
    javaClass,
    JavaRunnable,
    javaString
}

import com.intellij.facet {
    FacetManager
}
import com.intellij.openapi.application {
    WriteAction,
    Result,
    ApplicationManager {
        application
    },
    ModalityState
}
import com.intellij.openapi.extensions {
    Extensions
}
import com.intellij.openapi.\imodule {
    Module,
    ModuleUtil
}
import com.intellij.openapi.project {
    DumbService {
        dumbService=getInstance
    }
}
import com.intellij.openapi.roots {
    ModuleRootManager,
    OrderRootType,
    LibraryOrderEntry
}
import com.intellij.openapi.ui {
    Messages
}
import com.intellij.openapi.util {
    Key
}
import com.intellij.openapi.vfs {
    VirtualFile,
    VfsUtil,
    VirtualFileManager {
        virtualFileManager=instance
    },
    JarFileSystem
}
import com.redhat.ceylon.cmr.api {
    ArtifactContext
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.compiler.typechecker.context {
    Context
}
import com.redhat.ceylon.compiler.typechecker.util {
    ModuleManagerFactory
}
import com.redhat.ceylon.ide.common.model {
    CeylonProject,
    BuildHook
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}
import com.redhat.ceylon.ide.common.typechecker {
    IdePhasedUnit
}
import com.redhat.ceylon.ide.common.util {
    BaseProgressMonitorChild
}
import com.redhat.ceylon.model.typechecker.model {
    Package
}
import com.redhat.ceylon.model.typechecker.util {
    TCModManager=ModuleManager
}

import java.io {
    File,
    IOException
}
import java.lang {
    Void,
    System,
    Thread
}

import org.intellij.plugins.ceylon.ide.ceylonCode {
    ITypeCheckerInvoker
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs {
    IdeaVirtualFolder
}
import com.redhat.ceylon.model.cmr {
    ArtifactResult
}
import com.intellij.openapi.externalSystem.service.project {
    IdeModifiableModelsProviderImpl
}
import com.intellij.psi {
    PsiFile
}

shared class IdeaCeylonProject(ideArtifact, model)
        extends CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile>() {
    variable Boolean languageModuleAdded = false;
    
    shared object nativeFolderProperties {
        shared Key<Package> packageModel = Key<Package>("CeylonPlugin.nativeFolder_packageModel");
        shared Key<IdeaVirtualFolder> root = Key<IdeaVirtualFolder>("CeylonPlugin.nativeFolder_root");
        shared Key<Boolean> rootIsSource = Key<Boolean>("CeylonPlugin.nativeFolder_rootIsSource");
    }

    object addModuleArchiveHook
            satisfies BuildHook<Module, VirtualFile, VirtualFile, VirtualFile> {

        ArtifactResult? findLanguageCar() {
            String moduleName = "ceylon.language";
            String moduleVersion = TypeChecker.languageModuleVersion;

            return repositoryManager.getArtifactResult(
                ArtifactContext(null, moduleName, moduleVersion, ArtifactContext.car)
            );
        }

        shared actual void beforeClasspathResolution(CeylonProjectBuildAlias build, CeylonProjectBuildAlias.State state) {
            Thread.currentThread().contextClassLoader = javaClass<IdeaCeylonProject>().classLoader;
            if (! languageModuleAdded) {
                if (exists languageModuleArtifact = findLanguageCar()) {
                    value runnable = JavaRunnable(() {
                        try {
                            addLibrary(languageModuleArtifact, true);
                        } catch (IOException e) {
                            platformUtils.log(Status._ERROR,
                                "Can't add ceylon language to classpath", e);
                        }
                    });
                    application.invokeAndWait(runnable, ModalityState.any());

                    dumbService(model.ideaProject).waitForSmartMode();
                    languageModuleAdded = true;
                } else {
                    platformUtils.log(Status._ERROR, "Could not locate ceylon.language.car");
                }
            }
        }

        shared actual void repositoryManagerReset(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject) {
            languageModuleAdded = false;
        }
    }


    shared actual Module ideArtifact;
    shared actual IdeaCeylonProjects model;
    shared actual String name => ideArtifact.name;

    shared actual Boolean loadBinariesFirst => true;

    shared actual Boolean loadInterProjectDependenciesFromSourcesFirst => true;
            
    shared Module ideaModule => ideArtifact;

    VirtualFile getDefaultRoot() {
        if (exists file = ideaModule.moduleFile) {
            return file.parent;
        }

        value path = ideaModule.moduleFilePath;
        Integer? lastSlash = path.lastOccurrence('/');
        if (exists lastSlash) {
            String parentPath = path.span(0, lastSlash);
            assert(exists file = virtualFileManager
                .findFileByUrl("file://``parentPath``"));
            return file;
        }

        if (application.unitTestMode) {
            return ideaModule.project.baseDir;
        }
        throw Exception("Couldn't get module root for ``path``");
    }

    shared VirtualFile moduleRoot
            => let (defaultRoot = getDefaultRoot())
                    if (exists contentsRoot = ModuleRootManager
                            .getInstance(ideaModule)?.contentRoots,
                        contentsRoot.array.size == 1)
                        then ( contentsRoot.array.first else defaultRoot)
                        else defaultRoot;

    shared actual File rootDirectory
            => VfsUtil.virtualToIoFile(moduleRoot);

    VirtualFile? findModuleFile(String moduleRelativePath)
            => moduleRoot.findFileByRelativePath(moduleRelativePath);

    VirtualFile? findModuleFileWithRefresh(String moduleRelativePath)
            => VfsUtil.findFileByIoFile(File(rootDirectory, moduleRelativePath), true);

    shared actual void createNewOutputFolder(String relativePath) {
        function createDirectory()
            =>  object extends WriteAction<Void>() {
                    shared actual void run(Result<Void> result) {
                        VfsUtil.createDirectoryIfMissing(moduleRoot, relativePath);
                    }
                }.execute().throwException();

        if (exists outputFolder = findModuleFileWithRefresh(relativePath)) {
            outputFolder.refresh(false, false);
            if (! outputFolder.\iexists()) {
                createDirectory();
            }
        } else {
            createDirectory();
        }
    }

    shared actual void deleteOldOutputFolder(String folderProjectRelativePath) {
        VirtualFile? oldOutputRepoFolder = findModuleFile(folderProjectRelativePath);
        if(exists oldOutputRepoFolder) {
            if (Messages.showYesNoDialog(ideaModule.project,
                    "The Ceylon output repository has changed.
                     Do you want to remove the old output repository folder \
                     '`` oldOutputRepoFolder.path ``' and all its contents ?",
                    "Changing Ceylon output repository",
                    Messages.questionIcon) == Messages.yes) {
                try {
                    object extends WriteAction<Void>() {
                        shared actual void run(Result<Void> result) {
                            oldOutputRepoFolder.delete(outer);
                        }
                    }.execute().throwException();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // TODO : if we keep the old output folder, remove the "Derived flag"

            }
        }
    }

    shared actual Boolean hasConfigFile
            => findModuleFile(ceylonConfigFileProjectRelativePath) exists;

    shared actual void refreshConfigFile() {
        value configFile = findModuleFile(ceylonConfigFileProjectRelativePath);
        if (exists configFile) {
            // TODO check if this is OK to do a synchronous refresh
            configFile.refresh(false, false);
        } else {
            moduleRoot.refresh(false, false);
        }
    }

    // TODO : needs to be implemented
    shared actual Boolean synchronizedWithConfiguration => true;
    
    shared actual Boolean compileToJava
            => ideConfiguration.compileToJvm else false;
    
    shared actual Boolean compileToJs
            => ideConfiguration.compileToJs else false;

    shared actual void createOverridesProblemMarker(Exception ex, File absoluteFile, Integer overridesLine, Integer overridesColumn) {
        // TODO
    }
    
    shared actual void removeOverridesProblemMarker() {
        // TODO
    }
    
    shared actual String systemRepository 
            => interpolateVariablesInRepositoryPath(ideConfiguration.systemRepository else "${ceylon.repo}");
    
    String interpolateVariablesInRepositoryPath(String repoPath) {
        String userHomePath = System.getProperty("user.home");
        value ext = Extensions.getExtensions(ITypeCheckerInvoker.epName).get(0);
        String pluginRepoPath = ext.embeddedCeylonRepository.absolutePath;
        return repoPath.replace("${user.home}", userHomePath).replace("${ceylon.repo}", pluginRepoPath);
    }

    shared actual void completeCeylonModelParsing(BaseProgressMonitorChild monitor) {}
    
    shared actual ModuleManagerFactory moduleManagerFactory => 
            object satisfies ModuleManagerFactory {
        
                createModuleManager(Context context)
                        => IdeaModuleManager(context.repositoryManager, model, outer);
                
                shared actual IdeaModuleSourceMapper
                createModuleManagerUtil(Context context, TCModManager moduleManager) {
                    assert(is IdeaModuleManager moduleManager);
                    return IdeaModuleSourceMapper(context, moduleManager);
                }
            };
    
    shared void addLibrary(ArtifactResult artifact, Boolean clear = false) {
        value lock = application.acquireWriteActionLock(javaClass<IdeaCeylonProject>());
        value provider = IdeModifiableModelsProviderImpl(ideArtifact.project);
        value mrm = provider.getModifiableRootModel(ideArtifact);

        try {
            if (clear) {
                for (e in mrm.orderEntries) {
                    if (is LibraryOrderEntry e,
                        exists libName = e.libraryName,
                        libName.startsWith("Ceylon: ") || libName == "Ceylon dependencies") {
                        mrm.removeOrderEntry(e);
                    }
                }
            }

            value libraryName = "Ceylon: " + artifact.name() + "/" + artifact.version();
            value lib = provider.getLibraryByName(libraryName)
                else provider.createLibrary(libraryName);
            value libModel = provider.getModifiableLibraryModel(lib);

            void updateUrl(OrderRootType type, VirtualFile file) {
                if (!libModel.getUrls(type).iterable.contains(javaString(file.string))) {
                    libModel.addRoot(file, type);
                }
            }


            value carFile = VirtualFileManager.instance
                .findFileByUrl(JarFileSystem.protocolPrefix + artifact.artifact().canonicalPath + JarFileSystem.jarSeparator);

            if (exists carFile) {
                updateUrl(OrderRootType.classes, carFile);
            }

            value sourceContext = ArtifactContext(null, artifact.name(), artifact.version(), ArtifactContext.src);
            if (exists sourceArtifact = repositoryManager.getArtifactResult(sourceContext)) {
                value srcFile = VirtualFileManager.instance
                    .findFileByUrl(JarFileSystem.protocolPrefix + sourceArtifact.artifact().canonicalPath + JarFileSystem.jarSeparator);
                
                if (exists srcFile) {
                    updateUrl(OrderRootType.sources, srcFile);
                }
            }

            if (!exists entry = mrm.findLibraryOrderEntry(lib)) {
                mrm.addLibraryEntry(lib);
            }

            provider.commit();
        } catch (e) {
            platformUtils.log(Status._ERROR, "Couldn't add library", e);
            provider.dispose();
        } finally {
            lock.finish();
        }
    }

    shared Boolean isAndroid {
        for (f in FacetManager.getInstance(ideaModule).allFacets) {
            if (f.type.id.string == "android-gradle") {
                return true;
            }
        }

        return false;
    }

    value srcPath => "src/main/ceylon";
    value otherSrcPath => "build/generated/source/r/debug";
    value repoPath => "./build/intermediates/ceylon-android/repository";

    shared void setupForAndroid(String jdkProvider) {
        if (!configuration.projectSourceDirectories.contains(srcPath)) {
            configuration.projectSourceDirectories = {
                srcPath,
                otherSrcPath,
                *configuration.projectSourceDirectories
            };
        }

        if (!configuration.projectLocalRepos.contains(repoPath)) {
            configuration.projectLocalRepos = {
                repoPath,
                *configuration.projectLocalRepos
            };
        }

        configuration.projectJdkProvider = jdkProvider;
    }

    shared void clean() {
        sourceNativeFolders.each(removeFolderFromModel);
    }

    buildHooks => { addModuleArchiveHook };
}

shared IdeaCeylonProject? findProjectForFile(PsiFile file) {

    if (is CeylonFile file,
        is IdePhasedUnit pu = file.localAnalysisResult?.lastPhasedUnit,
        is IdeaModuleSourceMapper msm = pu.moduleSourceMapper,
        is IdeaCeylonProject project = msm.ceylonProject) {

        return project;
    }
    if (exists projects = file.project.getComponent(javaClass<IdeaCeylonProjects>()),
        exists mod = ModuleUtil.findModuleForFile(file.virtualFile, file.project),
        is IdeaCeylonProject project = projects.getProject(mod)) {
        
        return project;
    }
    
    return null;
}