import com.intellij.codeInsight.completion {
    CompletionParameters,
    CompletionResultSet,
    CompletionProvider,
    CompletionService {
        completionService
    },
    CompletionInitializationContext
}
import com.intellij.codeInsight.lookup {
    LookupElementWeigher,
    LookupElement
}
import com.intellij.psi.impl.source.tree {
    LeafPsiElement
}
import com.intellij.util {
    ProcessingContext
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.ide.common.completion {
    completionManager
}
import com.redhat.ceylon.ide.common.settings {
    CompletionOptions
}

import java.lang {
    Comparable,
    JInteger=Integer
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model.parsing {
    DummyProgressMonitor
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    CeylonTokens
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    findProjectForFile,
    concurrencyManager
}


shared abstract class IdeaCompletionProvider() extends CompletionProvider<CompletionParameters>()  {
    
    shared formal CompletionOptions options;
    
    shared actual void addCompletions(CompletionParameters parameters, 
        ProcessingContext context, variable CompletionResultSet result) {
        
        value sorter = completionService.emptySorter().weigh(
            object extends LookupElementWeigher("keepInitialOrderWeigher", false, false) {
                variable Integer i = 0;
                
                shared actual Comparable<JInteger> weigh(LookupElement element) {
                    i++;
                    return JInteger(i);
                }
            }
        );
        
        result = result.withRelevanceSorter(sorter);

        if (is LeafPsiElement position = parameters.position) {
            if (position.elementType == CeylonTokens.astringLiteral) {
                result = result.withPrefixMatcher("");
            }
            if (position.elementType == CeylonTokens.pidentifier) {
                // In case of a package identifier like `ceylon.collection`, we compute a reference
                // on the whold path, which will lead IntelliJ to create prefixes like `ceylon.col`
                // whereas completionManager will return things like `collection`, which won't match
                // the prefix. We thus have to change the prefix to what's after the dot.
                value prefix = position.text.spanTo(position.text.size
                    - CompletionInitializationContext.dummyIdentifierTrimmed.size - 1);
                result = result.withPrefixMatcher(prefix);
            }
        }
        
        concurrencyManager.withAlternateResolution(() {
            if (exists element = parameters.originalPosition,
                is CeylonFile ceylonFile = element.containingFile,
                exists pu = ceylonFile.ensureTypechecked()) {
                addCompletionsInternal(parameters, context, result, pu, options);
            }
        });
    }
    
    void addCompletionsInternal(CompletionParameters parameters, 
        ProcessingContext context, CompletionResultSet result,
        PhasedUnit pu, CompletionOptions options) {
        
        
        value isSecondLevel = parameters.invocationCount > 0 && parameters.invocationCount % 2 == 0;
        value element = parameters.originalPosition;
        value doc = parameters.editor.document;
        assert(is CeylonFile ceylonFile = element.containingFile);
        value project = findProjectForFile(ceylonFile);
        value params = IdeaCompletionContext(ceylonFile, parameters.editor, project, options);

        completionManager.getContentProposals {
            typecheckedRootNode = pu.compilationUnit;
            ctx = params;
            offset = parameters.editor.caretModel.offset;
            line = doc.getLineNumber(element.textOffset);
            secondLevel = isSecondLevel;
            monitor = DummyProgressMonitor.wrap("");
            // The parameters tooltip has nothing to do with code completion, so we bypass it
            returnedParamInfo = true;
        };
        
        CustomLookupCellRenderer.install(parameters.editor.project);
        
        for (completion in params.proposals.proposals) {
            result.addElement(completion);
        }
        
        if (!isSecondLevel) {
            result.addLookupAdvertisement("Call again to toggle second-level completions");
        }
    }
}
