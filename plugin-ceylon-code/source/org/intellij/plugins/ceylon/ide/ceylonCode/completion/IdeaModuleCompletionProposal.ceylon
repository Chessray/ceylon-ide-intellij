import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.cmr.api {
    ModuleVersionDetails,
    ModuleSearchResult
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node
}
import com.redhat.ceylon.ide.common.completion {
    ModuleProposal
}
import com.redhat.ceylon.ide.common.platform {
    LinkedMode
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

class IdeaModuleCompletionProposal(Integer offset, String prefix,
        Integer len, String versioned, ModuleSearchResult.ModuleDetails mod,
        Boolean withBody, ModuleVersionDetails version, 
        String name, Node node, CompletionData data) 
        extends ModuleProposal<LookupElement>
        (offset, prefix, len, versioned, mod, withBody, version, name, node, data)
        satisfies IdeaCompletionProposal {
   
    shared LookupElement lookupElement => newLookup(versioned, versioned.spanFrom(len),
       ideaIcons.modules, object satisfies InsertHandler<LookupElement> {
           shared actual void handleInsert(InsertionContext insertionContext,
               LookupElement? t) {
               
               // Undo IntelliJ's completion
               value platformDoc = data.commonDocument;
               replaceInDoc(platformDoc, offset, text.size - prefix.size, "");
               
               applyInternal(platformDoc);
           }
        }
    );
    
    shared actual LookupElement newModuleProposal(ModuleVersionDetails d, DefaultRegion selection, LinkedMode lm)
            => newLookup(d.version, d.version, null, null, TextRange.from(selection.start, selection.length)); // TODO icon
    
    shared actual Boolean toggleOverwrite => false;
}
