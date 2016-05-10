import com.redhat.ceylon.ide.common.correct {
    splitIfStatementQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class SplitIfStatementIntention() extends GenericIntention() {
    
    familyName => "Split if statement";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);

        splitIfStatementQuickFix.addSplitIfStatementProposal(data, statement);
    }
}
