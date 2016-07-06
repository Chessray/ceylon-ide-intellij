package org.intellij.plugins.ceylon.ide.doc;

import ceylon.language.Tuple;
import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.util.TextRange;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiElementBase;
import com.intellij.psi.tree.IElementType;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.correct.specifyTypeQuickFix_;
import com.redhat.ceylon.ide.common.typechecker.LocalAnalysisResult;
import com.redhat.ceylon.ide.common.util.nodes_;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import org.intellij.plugins.ceylon.ide.annotator.TypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.correct.IdeaQuickFixData;
import org.intellij.plugins.ceylon.ide.ceylonCode.doc.IdeaDocGenerator;
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting.highlighter_;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.intellij.plugins.ceylon.ide.ceylonCode.lightpsi.CeylonLightElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.ConcurrencyManagerForJava;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTokens;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.descriptions_;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.DeclarationPsiNameIdOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import static com.redhat.ceylon.ide.common.util.toJavaString_.toJavaString;
import static org.intellij.plugins.ceylon.ide.ceylonCode.resolve.resolveDeclaration_.resolveDeclaration;

public class CeylonDocProvider extends AbstractDocumentationProvider {

    private static final Logger LOGGER = Logger.getInstance(CeylonDocProvider.class);

    // A few common element types we know will never trigger a doc popup
    private static final List<IElementType> TYPES_TO_IGNORE = Arrays.asList(
            CeylonTokens.WS, CeylonTokens.LINE_COMMENT, CeylonTokens.COMMA, CeylonTokens.SEMICOLON,
            CeylonTokens.LBRACE, CeylonTokens.RBRACE,
            CeylonTokens.LBRACKET, CeylonTokens.RBRACKET,
            CeylonTokens.LPAREN, CeylonTokens.RPAREN
    );

    private descriptions_ provider
            = descriptions_.get_();

    private highlighter_ highlighter = highlighter_.get_();

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof CeylonCompositeElement) {
            String str = toJavaString(provider.descriptionForPsi((CeylonCompositeElement) element));
            if (str != null) {
                return highlighter.highlight(str, element.getProject())
                        // font tags are removed in com.intellij.util.ui.UIUtil.getHtmlBody(com.intellij.util.ui.Html),
                        // so we have to do a little trick to keep colors
                        .replaceAll("<font", "<span").replaceAll("</font>", "</span>");
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String generateDoc(final PsiElement element, @Nullable PsiElement originalElement) {
        try {
            TypeChecker tc = TypeCheckerProvider.getFor(element);
            if (tc == null) {
                tc = TypeCheckerProvider.getFor(originalElement);
            }
            if (tc == null) {
                System.err.println("Can't get type checker for element " + element);
                return null;
            }
            IdeaDocGenerator generator = new IdeaDocGenerator(tc);
            if (element instanceof DummyPsiElement) {
                Referenceable referenceable = ((DummyPsiElement) element).referenceable;
                LocalAnalysisResult localAnalysisResult = ((CeylonFile) element.getContainingFile()).getLocalAnalysisResult();
                PhasedUnit pu = localAnalysisResult.getLastPhasedUnit();
                Tree.CompilationUnit cu = ((CeylonFile) element.getContainingFile()).getCompilationUnit();
                return generator.getDocumentationText(referenceable, null, cu, generator.DocParams$new$(pu, element.getProject())).value;
            }
            if (element.getContainingFile() != null) {
                final PhasedUnit[] puRef = new PhasedUnit[] { null };
                ConcurrencyManagerForJava.withAlternateResolution(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        puRef[0] = ((CeylonFile) element.getContainingFile()).ensureTypechecked();
                        return null;
                    }
                });
                PhasedUnit pu = puRef[0];
                if (pu == null) {
                    LOGGER.warn("No phased unit for file " + element.getContainingFile().getVirtualFile().getPath());
                } else {
                    Tree.CompilationUnit cu = pu.getCompilationUnit();
                    IdeaDocGenerator.DocParams params = generator.DocParams$new$(pu, element.getProject());

                    ceylon.language.String doc;
                    if (element instanceof CeylonLightElement) {
                        doc = generator.getDocumentationText(((CeylonLightElement) element).getDeclaration(),
                                null, cu, params);
                    } else {
                        doc = generator.getDocumentation(cu, element.getTextRange().getStartOffset(),
                                params);
                    }
                    return Objects.toString(doc, null);
                }
            }
        } catch (ceylon.language.AssertionError | Exception e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        if (contextElement != null && !TYPES_TO_IGNORE.contains(contextElement.getNode().getElementType())) {
            return contextElement;
        }

        return null;
    }

    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        if (object instanceof Tuple) {
            Object first = ((Tuple) object).getFirst();
            if (first instanceof Declaration) {
                PsiElement target = resolveDeclaration((Declaration) first, element.getProject());
                if (target instanceof DeclarationPsiNameIdOwner) {
                    return ((DeclarationPsiNameIdOwner) target).getNameIdentifier();
                }
                return target;
            }
        }

        return null;
    }

    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, final PsiElement context) {
        TypeChecker tc = TypeCheckerProvider.getFor(context);
        if (tc == null) {
            return null;
        }
        LocalAnalysisResult localAnalysisResult = ((CeylonFile) context.getContainingFile()).getLocalAnalysisResult();
        if (localAnalysisResult == null) {
            return null;
        }
        final Tree.CompilationUnit cu = localAnalysisResult.getTypecheckedRootNode();
        final PhasedUnit pu = localAnalysisResult.getLastPhasedUnit();

        if (cu == null || pu == null) {
            return null;
        }
        if (link.startsWith("stp:")) {
            int offset = Integer.parseInt(link.substring(4));
            final Node node = nodes_.get_().findNode(cu, null, offset, offset + 1);

            if (node instanceof Tree.Type) {
                JBPopup hint = DocumentationManager.getInstance(context.getProject()).getDocInfoHint();
                if (hint != null) {
                    hint.cancel();
                    context.getContainingFile().navigate(true);
                }

                new WriteCommandAction(context.getProject()) {
                    @Override
                    protected void run(@NotNull Result result) throws Throwable {
                        Document doc = context.getContainingFile().getViewProvider().getDocument(); // we should retrieve the document in LocalAnalysisResult to be perfectly consistent
                        IdeaQuickFixData data = new IdeaQuickFixData(null, doc, cu, pu, node, null, null, null);
                        specifyTypeQuickFix_.get_().createProposal((Tree.Type) node, data);
                    }
                }.execute();
                return context; // whatever value, we just want to avoid other providers being called
            }
        }

        IdeaDocGenerator gen = new IdeaDocGenerator(tc);
        Referenceable target = gen.getLinkedModel(new ceylon.language.String(link), gen.DocParams$new$(pu, context.getProject()));

        if (target != null) {
            if (link.startsWith("doc:")) {
                return new DummyPsiElement(target, context.getContainingFile());
            } else if (link.startsWith("dec:")) {
                PsiElement psiDecl = resolveDeclaration(target, context.getProject());
                if (psiDecl != null && psiDecl instanceof Navigatable) {
                    ((Navigatable) psiDecl).navigate(true);
                }
            }
        }

        return null;
    }

    /**
     * A trick to redirect a click on a link to generateDoc() without actually knowing which
     * PsiElement represents the target Referenceable.
     */
    private static class DummyPsiElement extends PsiElementBase {

        private Referenceable referenceable;
        private PsiFile containingFile;

        DummyPsiElement(Referenceable referenceable, PsiFile containingFile) {
            this.referenceable = referenceable;
            this.containingFile = containingFile;
        }

        @Override
        public ItemPresentation getPresentation() {
            return new ItemPresentation() {
                @Nullable
                @Override
                public String getPresentableText() {
                    return referenceable.getNameAsString();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Nullable
                @Override
                public Icon getIcon(boolean unused) {
                    return null;
                }
            };
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @NotNull
        @Override
        public Project getProject() {
            return containingFile.getProject();
        }

        @NotNull
        @Override
        public Language getLanguage() {
            return CeylonLanguage.INSTANCE;
        }

        @NotNull
        @Override
        public PsiElement[] getChildren() {
            return PsiElement.EMPTY_ARRAY;
        }

        @Override
        public PsiElement getParent() {
            return containingFile;
        }

        @Override
        public TextRange getTextRange() {
            return null;
        }

        @Override
        public int getStartOffsetInParent() {
            return 0;
        }

        @Override
        public int getTextLength() {
            return 0;
        }

        @Nullable
        @Override
        public PsiElement findElementAt(int offset) {
            return null;
        }

        @Override
        public int getTextOffset() {
            return 0;
        }

        @Override
        public String getText() {
            return null;
        }

        @NotNull
        @Override
        public char[] textToCharArray() {
            return new char[0];
        }

        @Override
        public ASTNode getNode() {
            return null;
        }
    }
}

