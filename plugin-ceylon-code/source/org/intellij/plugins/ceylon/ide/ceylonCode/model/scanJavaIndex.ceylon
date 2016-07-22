import ceylon.interop.java {
    javaString,
    CeylonIterable
}
import ceylon.language {
    langNull=null,
    langTrue=true,
    langFalse=false
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
    PsiClass,
    PsiModifierList
}
import com.intellij.util {
    Processor
}
import com.redhat.ceylon.ide.common.model.asjava {
    getJavaQualifiedName
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
    Unit,
    Package,
    Value
}

import java.io {
    File
}
import java.lang {
    JString=String
}
import java.util {
    HashMap,
    Collections {
        emptyList
    },
    JList=List
}

import org.intellij.plugins.ceylon.ide.ceylonCode.compiled {
    classFileDecompilerUtil
}

HashMap<JString,DeclarationWithProximity> scanJavaIndex(IdeaModule that, Unit sourceUnit,
    IdeaModuleManager moduleManager, Module mod, String startingWith, Integer proximity) {

    value result = HashMap<JString,DeclarationWithProximity>();
    value allDependencies = that.transitiveDependencies
        .narrow<IdeaModule>()
        .map((dep) => dep.artifact)
        .coalesced;

    value ceylonDependency = if (is IdeaModule lang = that.languageModule) then lang.artifact else null;

    object processor satisfies Processor<PsiClass> {
        String? findName(PsiClass cls) {
            value defaultName = (cls of PsiNamedElement).name;

            if (exists defaultName,
                defaultName.endsWith("_"),
                classFileDecompilerUtil.isCeylonCompiledFile(cls.containingFile.virtualFile)) {

                return defaultName.spanTo(defaultName.size - 2);
            }

            return defaultName;
        }

        function findOrCreateDeclaration(PsiClass cls, PsiModifierList modifiers, Package pkg) {

            if (!pkg.shared) {
                return null;
            }
            if (exists qName = cls.qualifiedName) {
                value imported = CeylonIterable(sourceUnit.imports).find(
                    (imp) => getJavaQualifiedName(imp.declaration) == qName
                );

                if (exists imported) {
                    return null;
                }
            }
            if (modifiers.findAnnotation(Annotations.container.className) exists
                || modifiers.findAnnotation(Annotations.ignore.className) exists) {
                return null;
            }

            value clsName = findName(cls);
            Declaration lightModel;

            if (cls.\iinterface) {
                lightModel = object extends Interface() {
                    variable Interface? lazyRealIntf = langNull;

                    Interface? computeRealIntf() {
                        if (is Interface decl = pkg.getMember(clsName, emptyList<Type>(), langFalse)) {
                            return decl;
                        }

                        return langNull;
                    }

                    Interface? realIntf => lazyRealIntf else (lazyRealIntf = computeRealIntf());

                    shared actual JList<TypeParameter> typeParameters => realIntf?.typeParameters else emptyList<TypeParameter>();
                    assign typeParameters {}
                    shared actual Type? type => realIntf?.type;
                };

            } else if (modifiers.findAnnotation(Annotations.method.className) exists) {
                lightModel = object extends Function() {
                    variable Function? lazyRealFunction = langNull;

                    Function? computeRealFunction() {
                        if (is Function decl = pkg.getMember(clsName, emptyList<Type>(), langFalse)) {
                            return decl;
                        }

                        return langNull;
                    }

                    Function? realFunction => lazyRealFunction else (lazyRealFunction = computeRealFunction());

                    parameterLists => realFunction?.parameterLists else emptyList<ParameterList>();

                    shared actual Type? type => realFunction?.type;
                    assign type {}

                    annotation = modifiers.findAnnotation(Annotations.annotationInstantiation.className) exists;
                };
            } else if (modifiers.findAnnotation(Annotations.\iobject.className) exists
                        || modifiers.findAnnotation(Annotations.attribute.className) exists) {
                lightModel = object extends Value() {
                    variable Value? lazyRealValue = langNull;

                    Value? computeRealValue() {
                        if (is Value decl = pkg.getMember(clsName, emptyList<Type>(), langFalse)) {
                            return decl;
                        }
                        return langNull;
                    }

                    Value? realValue => lazyRealValue else (lazyRealValue =computeRealValue());

                    shared actual Type? type => realValue ?. type;
                    assign type {}
                };
            } else if (modifiers.findAnnotation("com.redhat.ceylon.compiler.java.metadata.TypeAlias") exists) {
                return null; // TODO map to a TypeAlias
            } else {
                lightModel = object extends Class() {
                    variable Class? lazyRealClass = langNull;

                    Class? computeRealClass() {
                        if (exists decl = pkg.getMember(clsName, emptyList<Type>(), langFalse)) {
                            if (is Class decl) {
                                return decl;
                            } else {
                                print("Expected member of type Class but was ``className(decl)``");
                            }
                        }
                        return langNull;
                    }

                    Class? realClass => lazyRealClass else (lazyRealClass = computeRealClass());

                    parameterLists => realClass?.parameterLists else emptyList<ParameterList>();

                    shared actual JList<TypeParameter> typeParameters
                            => realClass?.typeParameters else emptyList<TypeParameter>();
                    assign typeParameters {}

                    shared actual Type? type => realClass?.type;

                    abstract = modifiers.hasModifierProperty(PsiModifier.abstract);
                    final = modifiers.hasModifierProperty(PsiModifier.final);
                    annotation = modifiers.findAnnotation(Annotations.annotationType.className) exists;
                };
            }

            lightModel.name = clsName;
            lightModel.container = pkg;
            lightModel.deprecated = modifiers.findAnnotation(Annotations.deprecated.className) exists;

            value unit = Unit();
            unit.\ipackage = pkg;
            lightModel.unit = unit;

            lightModel.shared = langTrue;

            return lightModel;
        }

        shared actual Boolean process(PsiClass cls) {
            if (exists modifiers = cls.modifierList,
                modifiers.hasExplicitModifier(PsiModifier.public),
                is PsiClassOwner file = cls.containingFile,
                exists pkg = moduleManager.modelLoader.findPackage(file.packageName)) {

                value lightModel = findOrCreateDeclaration(cls, modifiers, pkg);

                if (exists lightModel,
                    exists qname = cls.qualifiedName) {

                    value langPackage = pkg.languagePackage;
                    value prox = that.getProximity(proximity, langPackage, lightModel.name);
                    value dwp = DeclarationWithProximity(lightModel of Declaration, prox);
                    result.put(javaString(qname), dwp);
                }
            }
            return langTrue;
        }
    }
    object scope extends ModuleWithDependenciesScope(mod, ModuleWithDependenciesScope.libraries) {
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
    }

    value before = system.milliseconds;
    AllClassesGetter.processJavaClasses(CamelHumpMatcher(startingWith), mod.project, scope, processor);
    print("processed Java index in ``system.milliseconds - before``ms => ``result.size()`` results.");
    return result;
}