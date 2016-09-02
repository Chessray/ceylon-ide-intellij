import com.intellij.lang {
    ASTNode
}
import com.intellij.lang.folding {
    FoldingDescriptor,
    CustomFoldingBuilder
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.util {
    PsiTreeUtil
}

import java.util {
    List
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi,
    CeylonTokens,
    CeylonTypes
}

shared class CeylonFoldingBuilder() extends CustomFoldingBuilder() {

    isRegionCollapsedByDefault(ASTNode node)
            => node.elementType == CeylonTypes.importList;

    value blockElementTypes = [
        CeylonTypes.importModuleList,
        CeylonTypes.block,
        CeylonTypes.classBody,
        CeylonTypes.interfaceBody
//        CeylonTypes.importMemberOrTypeList
    ];

    shared actual String getLanguagePlaceholderText(ASTNode node, TextRange textRange) {
        if (node.elementType in blockElementTypes) {
            value text = node.text.normalized.trimmed;
            return text.size<40 then text else "{...}";
        } else {
            return "...";
        }
    }

    shared actual void buildLanguageFoldRegions(List<FoldingDescriptor> list,
            PsiElement element, Document document, Boolean quick) {
        variable PsiElement? current = element.firstChild;
        while (exists child = current) {
            appendDescriptors(child, (d) => list.add(d));
            buildLanguageFoldRegions(list, child, document, quick);
            current = child.nextSibling;
        }
    }

    void foldElement(PsiElement element, void add(FoldingDescriptor d)) {
        if (element.textLength>0) {
            add(FoldingDescriptor(element.node, element.textRange));
        }
    }

    void foldRange(Integer[2] range, PsiElement element, void add(FoldingDescriptor d)) {
        value [start, end] = range;
        if (end > start) {
            add(FoldingDescriptor(element.node, TextRange(start, end)));
        }
    }

    void foldAfterFirstLine(PsiElement element, void add(FoldingDescriptor d)) {
        if (exists newLine = element.text.firstOccurrence('\n')) {
            value start = element.textRange.startOffset + newLine;
            value end = element.textRange.endOffset;
            foldRange([start, end], element, add);
        }
    }

    void appendDescriptors(PsiElement element, void add(FoldingDescriptor d)) {

        if (element is CeylonPsi.BodyPsi) {
            foldElement(element, add);
        }
        else if (element is CeylonPsi.ImportModuleListPsi) {
            foldElement(element, add);
        }
        /*else if (element is CeylonPsi.ImportMemberOrTypeListPsi) {
            foldElement(element, add);
        }*/
        else if (element.node.elementType == CeylonTokens.multiComment) {
            foldAfterFirstLine(element, add);
        }
        else if (element.node.elementType == CeylonTokens.lineComment) {
            value prevlineComment
                    = if (exists prev = PsiTreeUtil.prevVisibleLeaf(element))
                    then prev.node.elementType == CeylonTokens.lineComment
                    else false;
            if (!prevlineComment) {
                variable PsiElement lastComment = element;
                while (exists next = PsiTreeUtil.nextVisibleLeaf(lastComment),
                       next.node.elementType == CeylonTokens.lineComment) {
                    lastComment = next;
                }
                if (lastComment != element) {
                    value start = element.textRange.endOffset-1;
                    value end = lastComment.textRange.endOffset-1;
                    foldRange([start, end], element, add);
                }
            }
        }
        else if (element is CeylonPsi.StringLiteralPsi) {
            foldAfterFirstLine(element, add);
        }
        else if (element is CeylonPsi.ImportListPsi,
                 element.textLength>0) {
            value start
                    = element.textRange.startOffset
                    + (element.text.startsWith("import ") then 7 else 0);
            value end = element.textRange.endOffset;
            foldRange([start, end], element, add);
        }

    }

}
