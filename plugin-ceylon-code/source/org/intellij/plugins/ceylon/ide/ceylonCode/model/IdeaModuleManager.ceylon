import com.redhat.ceylon.ide.common.model {
    IdeModuleManager,
    IdeModelLoader,
    BaseIdeModuleManager,
    BaseIdeModuleSourceMapper,
    BaseIdeModule,
    BaseCeylonProject
}
import com.intellij.openapi.\imodule {
    IJModule=Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.redhat.ceylon.model.typechecker.model {
    Modules,
    CeylonModule=Module
}
import com.intellij.openapi.roots {
    ModuleRootManager
}
import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    CeylonIterable
}
import com.redhat.ceylon.model.cmr {
    JDKUtils
}

shared class IdeaModuleManager(IdeaCeylonProject ceylonProject)
        extends IdeModuleManager<IJModule,VirtualFile,VirtualFile,VirtualFile>
        (ceylonProject) {
    
    shared actual Boolean moduleFileInProject(String moduleName,
        BaseCeylonProject? ceylonProject) { 
        // TODO
        return false;
    }
    
    shared actual IdeModelLoader newModelLoader(BaseIdeModuleManager self,
        BaseIdeModuleSourceMapper sourceMapper, Modules modules) { 
        return nothing;
    }
    
    shared actual BaseIdeModule newModule(String moduleName, String version) {
        value mod = ceylonProject.ideArtifact;
        value roots = ArrayList<VirtualFile>();
        
        if (moduleName.equals(CeylonModule.\iDEFAULT_MODULE_NAME)) {
            roots.addAll(ModuleRootManager.getInstance(mod)
                .getSourceRoots(true).array.coalesced);
        } else {
            value sr = ModuleRootManager.getInstance(mod).getSourceRoots(true);
            
            for (root in sr.iterable) {
                if (JDKUtils.isJDKModule(moduleName)) {
                    for (pkg in CeylonIterable(JDKUtils.getJDKPackagesByModule(moduleName))) {
                        // TODO
                        //if (root.getPackageFragment(pkg.string).\iexists()) {
                        //    roots.add(root);
                        //    break;
                        //}
                    }
                }
                else if (JDKUtils.isOracleJDKModule(moduleName)) {
                    for (pkg in CeylonIterable(JDKUtils.getOracleJDKPackagesByModule(moduleName))) {
                        // TODO
                        //if (root.getPackageFragment(pkg.string).\iexists()) {
                        //    roots.add(root);
                        //    break;
                        //}
                    }
                }
                // TODO else {}
            }
        }
        
        assert(is IdeaModuleSourceMapper msm = moduleSourceMapper);
        return IdeaModule(this, msm);
    }

}
