package org.intellij.plugins.ceylon.ide.templates;

import com.google.common.base.Objects;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelector;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelectorBase;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplatePsiInfo;
import com.intellij.codeInsight.template.postfix.templates.SurroundPostfixTemplateBase;
import com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.util.FindNodeVisitor;
import com.redhat.ceylon.model.typechecker.model.Type;
import org.intellij.plugins.ceylon.ide.CeylonFileType;
import org.intellij.plugins.ceylon.ide.annotator.SourceCodeVirtualFile;
import org.intellij.plugins.ceylon.ide.annotator.TypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExistsPostfixTemplate extends SurroundPostfixTemplateBase {

    public static final PostfixTemplatePsiInfo PSI_INFO = new PostfixTemplatePsiInfo() {
        @NotNull
        @Override
        public PsiElement createExpression(@NotNull PsiElement context, @NotNull String prefix, @NotNull String suffix) {
            String expression = prefix + context.getText() + suffix;
            String content = "void z(){if(" + expression + ")}";
            PsiFile file = PsiFileFactory.getInstance(context.getProject()).createFileFromText("dummy.ceylon", CeylonFileType.INSTANCE, content);

            return PsiTreeUtil.getParentOfType(PsiUtilCore.getElementAtOffset(file, 12), CeylonPsi.ExistsConditionPsi.class);
        }

        @NotNull
        @Override
        public PsiElement getNegatedExpression(@NotNull PsiElement element) {
            return element;
        }
    };

    public static Condition<PsiElement> IS_EXPRESSION = new Condition<PsiElement>() {
        @Override
        public boolean value(PsiElement element) {
            return element instanceof CeylonPsi.ExpressionPsi;
        }
    };

    public static PostfixTemplateExpressionSelector selectorTopmost(Condition<PsiElement> additionalFilter) {
        return new PostfixTemplateExpressionSelectorBase(additionalFilter) {
            @Override
            protected List<PsiElement> getNonFilteredExpressions(@NotNull PsiElement context, @NotNull Document document, int offset) {
                return ContainerUtil.<PsiElement>createMaybeSingletonList(getTopmostExpression(context));
            }

            @Override
            protected Condition<PsiElement> getFilters(int offset) {
                return Conditions.and(super.getFilters(offset), getPsiErrorFilter());
            }

            @NotNull
            @Override
            public Function<PsiElement, String> getRenderer() {
                return JavaPostfixTemplatesUtils.getRenderer();
            }
        };
    }

    @Nullable
    public static CeylonPsi.ExpressionPsi getTopmostExpression(PsiElement context) {
        CeylonPsi.ExpressionPsi expr = PsiTreeUtil.getNonStrictParentOfType(context, CeylonPsi.ExpressionPsi.class);

        if (expr != null && isOptional(expr)) {
            return expr;
        }

        return null;
    }

    /*
     * This is sucky.
     */
    private static boolean isOptional(CeylonPsi.ExpressionPsi expr) {
        CeylonFile ceylonFile = (CeylonFile) expr.getContainingFile();
        final VirtualFile virtualFile = Objects.firstNonNull(ceylonFile.getVirtualFile(), ceylonFile.getUserData(CeylonPostfixTemplateProvider.ORIG_VFILE));
        TypeCheckerProvider typeCheckerProvider = ModuleUtil.findModuleForFile(virtualFile, ceylonFile.getProject()).getComponent(TypeCheckerProvider.class);

        // FIXME I'd prefer having an up-to-date CompilationUnit in CeylonFile instead of having to retrieve another one
        PhasedUnit phasedUnit = typeCheckerProvider.getTypeChecker().getPhasedUnit(new SourceCodeVirtualFile(ceylonFile) {
            @Override
            public String getPath() {
                return virtualFile.getPath();
            }
        });
        Tree.CompilationUnit compilationUnit = phasedUnit.getCompilationUnit();

        FindNodeVisitor visitor = new FindNodeVisitor(expr.getTextOffset() + 1, expr.getTextOffset() + 2);
        compilationUnit.visit(visitor);

        Node node = visitor.getNode();
        Type model = null;

        if (node instanceof Tree.QualifiedMemberExpression) {
            model = ((Tree.QualifiedMemberExpression) node).getPrimary().getTypeModel();
        } else if (node instanceof Tree.Term) {
            model = ((Tree.Term) node).getTypeModel();
        }

        if (model != null && model.isUnion()) {
            for (Type type : model.getCaseTypes()) {
                if (type.isNull()) {
                    return true;
                }
            }
        }

        return false;
    }

    public ExistsPostfixTemplate() {
        super("ex", "if (exists expr)", PSI_INFO, selectorTopmost(IS_EXPRESSION));
    }

    @NotNull
    @Override
    protected String getHead() {
        return "exists ";
    }

    @NotNull
    @Override
    protected Surrounder getSurrounder() {
        return new CeylonWithIfSurrounder();
    }
}
