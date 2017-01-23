import com.intellij.psi {
    PsiElement,
    PsiClass,
    PsiMethod,
    PsiFile
}
import com.intellij.psi.search.searches {
    DefinitionsScopedSearch {
        SearchParameters
    }
}
import com.intellij.util {
    Processor,
    QueryExecutor
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.ide.common.util {
    FindSubtypesVisitor,
    FindRefinementsVisitor
}
import com.redhat.ceylon.model.typechecker.model {
    TypeDeclaration,
    TypedDeclaration
}

import org.intellij.plugins.ceylon.ide.model {
    findProjectForFile,
    declarationFromPsiElement,
    getCeylonProject,
    concurrencyManager
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonTreeUtil {
        findPsiElement,
        getDeclaringFile
    },
    CeylonFile,
    CeylonPsi,
    isInSourceArchive
}

shared class CeylonImplementationsSearch()
        satisfies QueryExecutor<PsiElement,SearchParameters> {

    shared actual Boolean execute(SearchParameters queryParameters,
                                  Processor<PsiElement> consumer) {
        findImplementors(queryParameters.element, consumer.process);
        return true;
    }

    void findImplementors(PsiElement sourceElement, void consumer(PsiElement element)) {
        if (is PsiClass|PsiMethod sourceElement) {
            if (exists psiFile = sourceElement.containingFile,
                is TypeDeclaration|TypedDeclaration decl = declarationFromPsiElement(sourceElement),
                exists pus = getCeylonProject(psiFile)?.typechecker?.phasedUnits) {

                scanPhasedUnits {
                    decl = decl;
                    node = null;
                    sourceElement = sourceElement;
                    consumer = consumer;
                    for (pu in pus.phasedUnits) pu
                };
            }
        }
        else if (is CeylonPsi.DeclarationPsi sourceElement,
            exists node = sourceElement.ceylonNode,
            is TypeDeclaration|TypedDeclaration decl = node.declarationModel,
            is CeylonFile ceylonFile = sourceElement.containingFile,
            exists project = concurrencyManager.needReadAccess(()
                => findProjectForFile(ceylonFile)),
            exists modules = project.modules) {

            if (exists pus = project.typechecker?.phasedUnits) {
                scanPhasedUnits {
                    decl = decl;
                    node = node;
                    sourceElement = sourceElement;
                    consumer = consumer;
                    for (pu in pus.phasedUnits) pu
                };
            }

            if (isInSourceArchive(ceylonFile.realVirtualFile())) {
                for (mod in modules) {
                    scanPhasedUnits {
                        pus = mod.phasedUnits;
                        decl = decl;
                        node = node;
                        sourceElement = sourceElement;
                        consumer = consumer;
                    };
                }
            }
        }
    }

    void action(PsiFile? declaringFile, Node dnode,
            void consumer(PsiElement element)) {
        if (exists psiElement = findPsiElement(dnode, declaringFile)) {
            consumer(psiElement);
        }
    }

    function findImplementations(TypeDeclaration|TypedDeclaration decl, PhasedUnit pu) {
        switch (decl)
        case (is TypeDeclaration) {
            value vis = FindSubtypesVisitor(decl);
            pu.compilationUnit.visit(vis);
            return vis.declarationNodes;
        }
        case (is TypedDeclaration) {
            value vis = FindRefinementsVisitor(decl);
            pu.compilationUnit.visit(vis);
            return vis.declarationNodes;
        }
    }

    void scanPhasedUnits({PhasedUnit*} pus,
        TypeDeclaration|TypedDeclaration decl,
        Tree.Declaration? node, PsiElement sourceElement,
        void consumer(PsiElement element)) {

        for (pu in pus) {
            for (dnode in findImplementations(decl, pu)) {
                if (exists node) {
                    if (dnode==node) {
                        continue;
                    }
                    if (is Tree.Declaration dnode,
                        dnode.declarationModel.qualifiedNameString
                            == decl.qualifiedNameString) {
                        continue;
                    }
                }
                
                value declaringFile = getDeclaringFile(dnode.unit, sourceElement.project);
                if (is CeylonFile declaringFile) {
                    declaringFile.doWhenAnalyzed((_) => action(declaringFile, dnode, consumer));
                } else {
                    action(declaringFile, dnode, consumer);
                }
            }
        }
    }
}
