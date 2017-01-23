import com.intellij.psi {
    PsiTypeParameter,
    PsiNamedElement
}
import com.intellij.psi.impl.source {
    PsiClassReferenceType
}
import com.redhat.ceylon.model.loader.mirror {
    TypeParameterMirror,
    TypeMirror
}

import java.util {
    ArrayList
}

// We don't use `SmartPsiElementPointer`s because `psi` is accessed eagerly
class PSITypeParameter(PsiTypeParameter|PsiClassReferenceType psi) satisfies TypeParameterMirror {
    
    bounds = ArrayList<TypeMirror>();

    if (is PsiTypeParameter psi) {
        concurrencyManager.needReadAccess(() {
            for (bound in psi.extendsList.referencedTypes) {
                bounds.add(PSIType(bound));
            }
        });
    }

    shared actual String name;
    if (is PsiNamedElement psi) {
        name = psi.name else "<unknown>";
    } else {
        name = psi.className;
    }
    
    string => "PSITypeParameter[``name``]";    
}