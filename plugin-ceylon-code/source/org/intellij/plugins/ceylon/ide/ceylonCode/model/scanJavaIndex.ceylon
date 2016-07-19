import ceylon.interop.java {
    javaString
}
import ceylon.language {
    langNull = null,
    langTrue = true,
    langFalse = false
}
import com.intellij.codeInsight.completion {
    AllClassesGetter
}
import com.intellij.codeInsight.completion.impl {
    CamelHumpMatcher
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.\imodule.impl.scopes {
    ModuleWithDependenciesScope
}
import com.intellij.openapi.vfs {
    JarFileSystem,
    VirtualFile
}
import com.intellij.psi {
    PsiClassOwner,
    PsiModifier,
    PsiNamedElement,
    PsiClass
}
import com.intellij.util {
    Processor
}
import com.redhat.ceylon.model.typechecker.model {
    TypeParameter,
    DeclarationWithProximity,
    Function,
    Class,
    Type,
    Declaration,
    ParameterList,
    Interface,
    Unit
}
import java.io {
    File
}
import java.lang {
    JString = String
}
import java.util {
    HashMap,
    Collections
}
import org.intellij.plugins.ceylon.ide.ceylonCode.compiled {
    classFileDecompilerUtil
}

HashMap<JString,DeclarationWithProximity> scanJavaIndex(IdeaModule that,
    IdeaModuleManager moduleManager, Module mod, String startingWith) {

    value result = HashMap<JString,DeclarationWithProximity>();
    value allDependencies = that.transitiveDependencies
        .narrow<IdeaModule>()
        .map((dep) => dep.artifact)
        .coalesced;

    value ceylonDependency = if (is IdeaModule lang = that.languageModule) then lang.artifact else null;

    value processor = object satisfies Processor<PsiClass> {
        String? findName(PsiClass cls) {
            value defaultName = (cls of PsiNamedElement).name;

            if (exists defaultName,
                defaultName.endsWith("_"),
                classFileDecompilerUtil.isCeylonCompiledFile(cls.containingFile.virtualFile)) {

                return defaultName.spanTo(defaultName.size - 2);
            }

            return defaultName;
        }

        shared actual Boolean process(PsiClass cls) {
            if (exists modifiers = cls.modifierList,
                modifiers.hasExplicitModifier(PsiModifier.public),
                is PsiClassOwner file = cls.containingFile,
                exists pkg = moduleManager.modelLoader.findPackage(file.packageName)) {

                Declaration lightModel;

                if (cls.\iinterface) {
                    lightModel = Interface();
                } else if (modifiers.findAnnotation(Annotations.method.className) exists,
                    !modifiers.findAnnotation(Annotations.annotationInstantiation.className) exists) {
                    lightModel = object extends Function() {
                        variable Function? lazyRealFunction = langNull;

                        Function? computeRealClass() {
                            if (is Function decl = pkg.getMember((cls of PsiNamedElement).name, Collections.emptyList<Type>(), langFalse)) {

                                return decl;
                            }

                            return langNull;
                        }

                        Function? realFunction => lazyRealFunction else (lazyRealFunction =computeRealClass());

                        parameterLists => realFunction?.parameterLists else Collections.emptyList<ParameterList>();
                    };
                } else {
                    lightModel = object extends Class() {
                        variable Class? lazyRealClass = langNull;

                        Class? computeRealClass() {
                            if (is Class decl = pkg.getMember((cls of PsiNamedElement).name, Collections.emptyList<Type>(), langFalse)) {

                                return decl;
                            }

                            return langNull;
                        }

                        Class? realClass => lazyRealClass else (lazyRealClass =computeRealClass());

                        parameterLists => realClass?.parameterLists else Collections.emptyList<ParameterList>();
                        typeParameters => realClass?.typeParameters else Collections.emptyList<TypeParameter>();

                        objectClass => modifiers.findAnnotation(Annotations.\iobject.className) exists;
                        anonymous => objectClass || annotation;
                        annotation => modifiers.findAnnotation(Annotations.annotationInstantiation.className) exists;
                    };

                    lightModel.annotation = modifiers.findAnnotation(Annotations.annotationInstantiation.className) exists;
                }

                lightModel.name = findName(cls);
                lightModel.container = pkg;

                value unit = Unit();
                unit.\ipackage = pkg;
                lightModel.unit = unit;

                lightModel.shared = langTrue;

                value dwp = DeclarationWithProximity(lightModel of Declaration, 0);
                if (exists qname = cls.qualifiedName) {
                    result.put(javaString(qname), dwp);
                }
            }
            return langTrue;
        }
    };
    value scope = object extends ModuleWithDependenciesScope(mod, ModuleWithDependenciesScope.libraries) {
        shared actual Boolean contains(VirtualFile file) {
            if (exists jar = JarFileSystem.instance.getVirtualFileForJar(file)) {
                // skip inner and internal classes
                if (file.name.contains('$')) {
                    return false;
                }

                // automatically add language declarations
                value jarFile = File(jar.path);
                if (exists ceylonDependency, ceylonDependency == jarFile) {
                    return true;
                }

                // don't go further if the prefix is empty
                if (startingWith.empty) {
                    return false;
                }

                // accept dependencies of the current module
                if (allDependencies.contains(jarFile)) {
                    return true;
                }

                // check for Java modules and other dependencies that have no artifact
                value sep = file.path.indexOf(JarFileSystem.jarSeparator);
                if (sep>0) {
                    value entryPath = file.path.spanFrom(sep + JarFileSystem.jarSeparator.size);

                    if (exists sep2 = entryPath.lastIndexWhere('/'.equals)) {
                        value pkg = entryPath.spanTo(sep2 - 1).replace("/", ".");
                        if (moduleManager.modelLoader.findPackage(pkg) exists) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    };

    value before = system.milliseconds;
    AllClassesGetter.processJavaClasses(CamelHumpMatcher(startingWith), mod.project, scope, processor);
    print("processed Java index in ``system.milliseconds - before``ms => ``result.size()`` results.");
    return result;
}