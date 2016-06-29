import ceylon.interop.java {
    javaClass
}

import com.intellij.lang.cacheBuilder {
    DefaultWordsScanner
}
import com.intellij.lang.findUsages {
    FindUsagesProvider
}
import com.intellij.openapi.diagnostic {
    Logger
}
import com.intellij.psi {
    PsiElement,
    PsiNamedElement
}
import com.intellij.psi.tree {
    TokenSet
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import java.lang {
    UnsupportedOperationException
}

import org.intellij.plugins.ceylon.ide.ceylonCode.parser {
    CeylonAntlrToIntellijLexerAdapter
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonCompositeElement,
    CeylonFile,
    CeylonPsi,
    TokenTypes,
    kind
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl {
    DeclarationPsiNameIdOwner
}

shared class CeylonFindUsagesProvider() satisfies FindUsagesProvider {

    value logger = Logger.getInstance(javaClass<CeylonFindUsagesProvider>());

    wordsScanner
            => DefaultWordsScanner(CeylonAntlrToIntellijLexerAdapter(),
                TokenSet.create(TokenTypes.lidentifier.tokenType,
                                TokenTypes.uidentifier.tokenType),
                TokenSet.create(TokenTypes.multiComment.tokenType,
                                TokenTypes.lineComment.tokenType),
                TokenSet.create(TokenTypes.stringLiteral.tokenType));

    canFindUsagesFor(PsiElement psiElement)
            => psiElement is DeclarationPsiNameIdOwner;

    getHelpId(PsiElement psiElement)
            => "Please open an issue if you ever need this help :)";

    getType(PsiElement element) => kind(element);

    shared actual String getDescriptiveName(PsiElement element) {
        if (is CeylonCompositeElement element) {
            assert (is CeylonFile file = element.containingFile);
            if (exists localAnalysisResult
                    = file.localAnalysisResult,
                exists lastCompilationUnit
                    = localAnalysisResult.lastCompilationUnit) {
                value node = nodes.findNode {
                    node = lastCompilationUnit;
                    tokens = localAnalysisResult.tokens;
                    startOffset = element.textRange.startOffset;
                    endOffset = element.textRange.endOffset;
                };
                switch (node)
                case (null) {}
                case (is Tree.InitializerParameter) {
                    return node.identifier.text;
                }
                else if (exists id = nodes.findDeclaration(lastCompilationUnit, node)?.identifier) {
                    return id.text;
                }
            }
        }

        logger.warn("Descriptive name not implemented for " + className(element));

        if (is CeylonPsi.IdentifierPsi element) {
            return element.ceylonNode.text;
        }
        return "<unknown>";
    }

    shared actual String getNodeText(PsiElement element, Boolean useFullName) {
        if (is PsiNamedElement element,
            exists name = element.name) {
            return name;
        }
        throw UnsupportedOperationException();
    }
}
