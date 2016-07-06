import com.intellij.lang {
    ASTNode,
    ParserDefinition,
    PsiParser
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    FileViewProvider
}
import com.intellij.psi.tree {
    TokenSet
}

import java.lang {
    UnsupportedOperationException
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    ceylonLanguage
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    CeylonPsiFactory,
    CeylonTypes,
    TokenTypes,
    IdeaCeylonParser
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl {
    SpecifierStatementPsiIdOwner
}

shared class CeylonParserDefinition() satisfies ParserDefinition {

    createLexer(Project project)
            => CeylonAntlrToIntellijLexerAdapter();

    fileNodeType = IdeaCeylonParser(ceylonLanguage);

    whitespaceTokens
            = TokenSet.create(TokenTypes.ws.tokenType);

    commentTokens
            = TokenSet.create(
                TokenTypes.lineComment.tokenType,
                TokenTypes.multiComment.tokenType);

    stringLiteralElements
            = TokenSet.create(
                TokenTypes.stringLiteral.tokenType,
                TokenTypes.stringStart.tokenType,
                TokenTypes.stringMid.tokenType,
                TokenTypes.stringEnd.tokenType,
                TokenTypes.charLiteral.tokenType);

    createElement(ASTNode node)
            => if (node.elementType == CeylonTypes.specifierStatement)
            then SpecifierStatementPsiIdOwner(node)
            else CeylonPsiFactory.createElement(node);

    createFile(FileViewProvider viewProvider)
            => CeylonFile(viewProvider);

    spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right)
            => SpaceRequirements.may;

    shared actual PsiParser createParser(Project project) {
        throw UnsupportedOperationException("See IdeaCeylonParser");
    }

}
