import ceylon.interop.java {
    javaString
}

import com.intellij.lang {
    ASTNode
}
import com.intellij.lang.annotation {
    Annotation,
    AnnotationHolder,
    Annotator
}
import com.intellij.openapi.editor.colors {
    TextAttributesKey
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.tree {
    IElementType
}

import java.util.regex {
    Pattern
}

import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    ceylonHighlightingColors
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi,
    CeylonPsiVisitor,
    CeylonTokens
}

shared class CeylonSyntaxAnnotator()
        extends CeylonPsiVisitor(false)
        satisfies Annotator {

    variable AnnotationHolder? holder = null;

    value docLinkPattern = Pattern.compile("""\[\[([^\]\[]*)\]\]""");
    value escapePattern = Pattern.compile("""\\[^{]|\\\{[^}]*\}""");

    value annotationHolder {
        assert (exists annotationHolder = holder);
        return annotationHolder;
    }

    shared actual void annotate(PsiElement element, AnnotationHolder holder) {
        this.holder = holder;
        element.accept(this);
        this.holder = null;
    }

    shared actual void visitCompilerAnnotationPsi(CeylonPsi.CompilerAnnotationPsi element) {
        super.visitCompilerAnnotationPsi(element);
        Annotation anno = annotationHolder.createInfoAnnotation(element, null);
        anno.textAttributes = ceylonHighlightingColors.annotation;
    }

    shared actual void visitMetaLiteralPsi(CeylonPsi.MetaLiteralPsi element) {
        super.visitMetaLiteralPsi(element);
        Annotation anno = annotationHolder.createInfoAnnotation(element, null);
        anno.textAttributes = ceylonHighlightingColors.meta;
    }

    /*shared actual void visitDocLinkPsi(CeylonPsi.DocLinkPsi element) {
        super.visitDocLinkPsi(element);
        value anno = annotationHolder.createInfoAnnotation(element, null);
        anno.textAttributes = ceylonHighlightingColors.interp;
    }*/

    function isWhitespace(PsiElement p)
            => p.node.elementType == CeylonTokens.ws;

    shared actual void visitStringTemplatePsi(CeylonPsi.StringTemplatePsi element) {
        super.visitStringTemplatePsi(element);
        for (PsiElement child in element.children) {
            if (is CeylonPsi.ExpressionPsi child) {
                variable PsiElement? prev = child;
                while (true) {
                    prev = prev?.prevSibling;
                    if (exists p = prev,
                        isWhitespace(p)) {
                    }
                    else {
                        break;
                    }
                }
                variable PsiElement? next = child;
                while (true) {
                    next = next?.nextSibling;
                    if (exists n = next,
                        isWhitespace(n)) {
                    }
                    else {
                        break;
                    }
                }
                Integer startOffset
                        = if (exists p = prev, p.text.endsWith("\`\`"))
                        then p.textRange.endOffset - 2
                        else child.textRange.startOffset;
                Integer endOffset
                        = if (exists n = next, n.text.startsWith("\`\`"))
                        then n.textRange.startOffset + 2
                        else child.textRange.endOffset;
                TextRange range = TextRange(startOffset, endOffset);
                Annotation anno = annotationHolder.createInfoAnnotation(range, null);
                anno.textAttributes = ceylonHighlightingColors.interpolation;
            }
        }
    }

    void createAnnotations(Pattern pattern, PsiElement element, TextAttributesKey key) {
        value offset = element.textOffset;
        value matcher = pattern.matcher(javaString(element.text));
        while (matcher.find()) {
            value anno = annotationHolder.createInfoAnnotation(
                TextRange(offset + matcher.start(0),
                          offset + matcher.end(0)),
                null);
            anno.textAttributes = key;
        }
    }

    shared actual void visitElement(PsiElement element) {
        super.visitElement(element);
        IElementType elementType = element.node.elementType;
        if (elementType == CeylonTokens.astringLiteral
         || elementType == CeylonTokens.averbatimString) {
            Annotation anno = annotationHolder.createInfoAnnotation(element, null);
            anno.textAttributes = ceylonHighlightingColors.annotationString;
            createAnnotations(docLinkPattern, element, ceylonHighlightingColors.docLink);
        }

        if (elementType == CeylonTokens.astringLiteral
         || elementType == CeylonTokens.stringLiteral
         || elementType == CeylonTokens.stringStart
         || elementType == CeylonTokens.stringEnd
         || elementType == CeylonTokens.stringMid
         || elementType == CeylonTokens.charLiteral) {
            createAnnotations(escapePattern, element, ceylonHighlightingColors.escape);
        }

        if (is CeylonPsi.StringTemplatePsi element) {
            visitStringTemplatePsi(element);
        }
    }

    shared actual void visitIdentifierPsi(CeylonPsi.IdentifierPsi element) {
        super.visitIdentifierPsi(element);
        PsiElement? prevSibling = element.prevSibling;
        ASTNode? firstChildNode = element.node.firstChildNode;
        if (!element.parent is CeylonPsi.ImportPathPsi,
            exists firstChildNode,
            firstChildNode.elementType == CeylonTokens.lidentifier,
            exists prevSibling,
            prevSibling.node.elementType == CeylonTokens.memberOp) {
            Annotation anno = annotationHolder.createInfoAnnotation(element, null);
            anno.textAttributes = ceylonHighlightingColors.member;
        }
        if (exists firstChildNode,
            firstChildNode.elementType == CeylonTokens.aidentifier) {
            Annotation anno = annotationHolder.createInfoAnnotation(element, null);
            anno.textAttributes = ceylonHighlightingColors.annotation;
        }
    }

    shared actual void visitImportPathPsi(CeylonPsi.ImportPathPsi element) {
        super.visitImportPathPsi(element);
        Annotation anno = annotationHolder.createInfoAnnotation(element, null);
        anno.textAttributes = ceylonHighlightingColors.packages;
    }
}
