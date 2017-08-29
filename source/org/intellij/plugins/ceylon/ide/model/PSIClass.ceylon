import com.intellij.lang.java {
    JavaLanguage
}
import com.intellij.psi {
    PsiClass,
    PsiModifier,
    PsiAnonymousClass,
    PsiMethod,
    PsiTypeParameter,
    PsiManager,
    PsiNameIdentifierOwner,
    SmartPsiElementPointer,
    PsiNamedElement
}
import com.intellij.psi.impl.compiled {
    ClsClassImpl
}
import com.intellij.psi.impl.light {
    LightMethodBuilder
}
import com.intellij.psi.impl.source {
    PsiExtensibleClass,
    ClassInnerStuffCache
}
import com.intellij.psi.util {
    PsiUtil,
    PsiTreeUtil {
        getContextOfType
    }
}
import com.redhat.ceylon.ide.common.model.mirror {
    IdeClassMirror
}
import com.redhat.ceylon.model.loader {
    AbstractModelLoader {
        getCacheKeyByModule
    }
}
import com.redhat.ceylon.model.loader.mirror {
    ClassMirror,
    TypeParameterMirror,
    FieldMirror,
    TypeMirror,
    MethodMirror
}
import com.redhat.ceylon.model.typechecker.model {
    Module
}

import java.util {
    ArrayList,
    Arrays
}

shared class PSIClass(SmartPsiElementPointer<PsiClass> psiPointer)
        extends PSIAnnotatedMirror(psiPointer)
        satisfies IdeClassMirror {

    shared PsiClass psi => get(psiPointer);

    shared Boolean valid => psiPointer.element exists;
    
    "This is needed when a PsiClass is removed from the index, and the model loader
     tries to unload the corresponding mirror. When that happens, we still need to access
     the qualified name although the PSI has been invalidated."
    variable value cacheQualifiedName
            = concurrencyManager.needReadAccess(() =>
                if (is PsiTypeParameter tp = psi)
                then PSIPackage(psiPointer).qualifiedName
                        + "."
                        + ((psi of PsiNamedElement).name else "")
                else (psi.qualifiedName else ""));

    variable String? cacheKey = null;
    getCacheKey(Module mod)
            => cacheKey
            else (cacheKey = getCacheKeyByModule(mod, qualifiedName));

    Boolean hasAnnotation(Annotations annotation)
        => let (cn = annotation.className)
            concurrencyManager.needReadAccess(() =>
                any {
                    if (exists mods = psi.modifierList)
                    for (ann in mods.annotations)
                    if (exists name = ann.qualifiedName)
                        name == cn
                });

    abstract => PsiUtil.isAbstractClass(get(psiPointer));
    
    annotationType => psi.annotationType;
    
    anonymous => psi is PsiAnonymousClass;
    
    ceylonToplevelAttribute => !innerClass && hasAnnotation(Annotations.attribute);
    
    ceylonToplevelMethod => !innerClass && hasAnnotation(Annotations.method);
    
    ceylonToplevelObject => !innerClass && hasAnnotation(Annotations.\iobject);

    value private => psi.hasModifierProperty(PsiModifier.private);

    defaultAccess => !(public || protected || private);

    typeParameters
            => concurrencyManager.needReadAccess(()
                => Arrays.asList<TypeParameterMirror>(
                    for (typeParam in psi.typeParameters)
                        PSITypeParameter(typeParam)));

    directFields
            => concurrencyManager.needReadAccess(()
                => Arrays.asList<FieldMirror>(
                    for (f in psi.fields)
                    if (!f.hasModifierProperty(PsiModifier.private)) // TODO !f.synthetic?
                        PSIField(pointer(f))));
    
    directInnerClasses
            => concurrencyManager.needReadAccess(()
                => Arrays.asList<ClassMirror>(
                    for (ic in psi.innerClasses)
                        PSIClass(pointer(ic))));
    
    directMethods
            => concurrencyManager.needReadAccess(() {
                value result = ArrayList<MethodMirror>();
                variable value hasCtor = false;

                for (m in psi.methods) {
                    if (m.constructor) {
                        hasCtor = true;
                    }
                    result.add(PSIMethod(pointer(m)));
                }

                if (psi.enum,
                    is PsiExtensibleClass ec = get(psiPointer)) {
                    value cache = ClassInnerStuffCache(ec);
                    if (exists valueOfMethod = cache.valueOfMethod) {
                        result.add(PSIMethod(pointer(valueOfMethod)));
                    }
                    if (exists valuesMethod = cache.valuesMethod) {
                        result.add(PSIMethod(pointer(valuesMethod)));
                    }
                }

                // unfortunately, IntelliJ does not include implicit default constructors in `psi.methods`
                if (!hasCtor) {
                    value builder
                            = LightMethodBuilder(
                                PsiManager.getInstance(psi.project),
                                JavaLanguage.instance,
                                (psi of PsiNameIdentifierOwner).name)
                            .addModifier("public")
                            .setConstructor(true);
                    result.add(PSIMethod(pointer(builder)));
                }
                return result;
            });
    
    enclosingClass
            => if (exists outerClass = getContextOfType(psi, `PsiClass`))
            then PSIClass(pointer(outerClass))
            else null;
    
    enclosingMethod
            => if (exists outerMeth = getContextOfType(psi, `PsiMethod`))
            then PSIMethod(pointer(outerMeth))
            else null;
    
    enum => psi.enum;
    
    final => psi.hasModifierProperty(PsiModifier.final);
    
    flatName => concurrencyManager.needReadAccess(() => psi.qualifiedName else "");

    innerClass => psi.containingClass exists
               || hasAnnotation(Annotations.container);

    \iinterface => psi.\iinterface;
    
    interfaces => concurrencyManager.needReadAccess(()
            => let (supertypes = psi.\iinterface
                    then psi.extendsListTypes
                    else psi.implementsListTypes)
                Arrays.asList<TypeMirror>(
                    for (t in supertypes)
                        PSIType(t)));
    
    javaSource => concurrencyManager.needReadAccess(()
                    => psi.containingFile?.name?.endsWith(".java") else false);

    loadedFromSource => javaSource;
    
    localClass => PsiUtil.isLocalClass(get(psiPointer))
               || hasAnnotation(Annotations.localContainer);
    
    \ipackage => PSIPackage(psiPointer);
     
    protected => psi.hasModifierProperty(PsiModifier.protected);
    
    public => psi.hasModifierProperty(PsiModifier.public);
    
    shared actual String qualifiedName {
        try {
            value qualifiedName
                    = if (is PsiTypeParameter tp = get(psiPointer))
                    then \ipackage.qualifiedName + "." + name
                    else concurrencyManager.needReadAccess(() => psi.qualifiedName else "");

            cacheQualifiedName = qualifiedName; // in case the class was renamed
            return qualifiedName;
        } catch (PsiElementGoneException e) {
            // PSI was invalidated, use the cached qualified name.
            return cacheQualifiedName;
        }
    }
    
    static => psi.hasModifierProperty(PsiModifier.static);
    
    shared actual TypeMirror? superclass {
        if (psi.\iinterface || qualifiedName == "java.lang.Object") {
            return null;
        }
        return concurrencyManager.needReadAccess(()
            // TODO check that the first element is always the superclass
            => if (exists st = psi.superTypes[0]) then PSIType(st) else null);
    }
    
    fileName => psi.containingFile?.name else "<unknown>";
    
    fullPath => if (exists f = psi.containingFile)
                then f.virtualFile.path.removeInitial("!/")
                else "<unknown>";
    
    isBinary => psi is ClsClassImpl;
    
    isCeylon => hasAnnotation(Annotations.ceylon);
    
}
