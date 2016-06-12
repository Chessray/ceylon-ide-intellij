import com.intellij.psi {
    PsiElement,
    PsiFile
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.editor {
    Editor
}
import com.redhat.ceylon.ide.common.refactoring {
    FindContainingExpressionsVisitor
}
import com.intellij.psi.util {
    PsiTreeUtil
}
import ceylon.interop.java {
    javaClass,
    createJavaObjectArray
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi,
    CeylonFile
}
import java.lang {
    ObjectArray,
    JString=String
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve {
    FindMatchingPsiNodeVisitor
}
import com.intellij.refactoring {
    IntroduceTargetChooser,
    RefactoringActionHandler
}
import com.intellij.openapi.util {
    Pass,
    TextRange
}
import com.intellij.util {
    Function
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.application {
    Result
}
import java.util {
    HashMap,
    List,
    ArrayList
}
import com.intellij.openapi.actionSystem {
    CommonDataKeys,
    LangDataKeys,
    DataContext
}
import com.intellij.openapi.actionSystem.impl {
    SimpleDataContext
}

shared abstract class AbstractExtractHandler() satisfies RefactoringActionHandler {

    shared formal TextRange? extract(Project project, Editor editor, CeylonFile file, TextRange range, Tree.Declaration? scope);

    shared default void extractToScope(Project project, Editor editor, CeylonFile file, TextRange range)
            => createAndIntroduceValue {
                proj = project;
                editor = editor;
                file = file;
                range = range;
                ceylonNode = null;
            };

    shared actual void invoke(Project project, Editor editor, PsiFile psiFile, DataContext dataContext) {
        if (is CeylonFile psiFile, psiFile.ensureTypechecked() exists) {
            extractSelectedExpression(project, editor, psiFile);
        }
    }

    shared actual void invoke(Project p, ObjectArray<PsiElement> elements, DataContext ctx) {
        // Do nothing
    }

    void extractSelectedExpression(Project project, Editor editor, CeylonFile file) {

        if (editor.selectionModel.selectionStart < editor.selectionModel.selectionEnd) {
            extractToScope {
                project = project;
                editor = editor;
                file = file;
                range = TextRange(editor.selectionModel.selectionStart,
                                  editor.selectionModel.selectionEnd);
            };
        }
        else {
            value visitor = FindContainingExpressionsVisitor(editor.caretModel.offset);
            visitor.visitAny(PsiTreeUtil.findChildOfType(file, javaClass<CeylonPsi.CompilationUnitPsi>()).ceylonNode);

            value allParentExpressions = toPsi(file, visitor.elements);
            if (allParentExpressions.empty) {
                //noop
            }
            else if (allParentExpressions.size() == 1) {
                extractToScope {
                    project = project;
                    editor = editor;
                    file = file;
                    range = allParentExpressions.get(0).textRange;
                };
            }
            else {
                IntroduceTargetChooser.showChooser(editor,
                    allParentExpressions,
                    object extends Pass<CeylonPsi.TermPsi>() {
                        pass(CeylonPsi.TermPsi selectedValue)
                                => extractToScope {
                            project = project;
                            editor = editor;
                            file = file;
                            range = selectedValue.textRange;
                        };
                    },
                    object satisfies Function<CeylonPsi.TermPsi,JString> {
                        fun(CeylonPsi.TermPsi element) => JString(element.text);
                    },
                    "Select expression");
            }
        }
    }

    shared void createAndIntroduceValue(Project proj, Editor editor, CeylonFile file, TextRange range, Tree.Declaration? ceylonNode) {

        if (exists newIdentifier = extract(proj, editor, file, range, ceylonNode)) {
            object extends WriteCommandAction<Nothing>(proj, file) {
                run(Result<Nothing> result) => file.forceReparse();
            }.execute();

            PsiElement? identifierElement = file.findElementAt(newIdentifier.startOffset);
            if (! exists identifierElement) {
                return;
            }

            assert (is CeylonPsi.DeclarationPsi inserted = identifierElement.parent.parent);
            PsiElement? identifier = PsiTreeUtil.getChildOfType(inserted, javaClass<CeylonPsi.IdentifierPsi>());
            if (! exists identifier) {
                return;
            }
            editor.caretModel.moveToOffset(identifier.textOffset);
            value myDataContext = HashMap<JString,Object>();
            myDataContext.put(JString(CommonDataKeys.editor.name), editor);
            myDataContext.put(JString(CommonDataKeys.psiFile.name), file);
            myDataContext.put(JString(LangDataKeys.psiElementArray.name), createJavaObjectArray<PsiElement>({ inserted }));
            value handler = CeylonVariableRenameHandler(true);
            handler.invoke(proj, editor, file, SimpleDataContext.getSimpleContext(myDataContext, null));
        }
    }

    List<CeylonPsi.TermPsi> toPsi(PsiFile file, ObjectArray<Tree.Term> elements) {
        value psi = ArrayList<CeylonPsi.TermPsi>();
        for (term in elements) {
            value visitor = FindMatchingPsiNodeVisitor(term, javaClass<CeylonPsi.TermPsi>());
            visitor.visitFile(file);

            if (is CeylonPsi.TermPsi element = visitor.psi) {
                psi.add(element);
            } else {
                print("Couldn't find PSI node for Node " + term.string);
            }
        }
        return psi;
    }
}