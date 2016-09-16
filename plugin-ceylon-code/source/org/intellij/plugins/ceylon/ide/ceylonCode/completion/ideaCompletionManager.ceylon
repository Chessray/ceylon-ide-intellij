import com.intellij.codeInsight.completion {
    CompletionParameters,
    CompletionResultSet,
    CompletionProvider,
    CompletionService {
        completionService
    },
    CompletionInitializationContext
}
import com.intellij.codeInsight.completion.impl {
    CompletionServiceImpl {
        completionServiceImpl=completionService
    }
}
import com.intellij.codeInsight.lookup {
    LookupElementWeigher,
    LookupElement
}
import com.intellij.openapi.application {
    ApplicationAdapter
}
import com.intellij.openapi.application.ex {
    ApplicationManagerEx {
        application=applicationEx
    }
}
import com.intellij.openapi.progress {
    EmptyProgressIndicator,
    ProcessCanceledException
}
import com.intellij.openapi.ui {
    MessageType
}
import com.intellij.psi.impl.source.tree {
    LeafPsiElement
}
import com.intellij.util {
    ProcessingContext
}
import com.redhat.ceylon.ide.common.completion {
    completionManager
}
import com.redhat.ceylon.ide.common.settings {
    CompletionOptions
}
import com.redhat.ceylon.ide.common.typechecker {
    LocalAnalysisResult
}

import java.lang {
    Comparable,
    JInteger=Integer
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    concurrencyManager,
    getModelManager,
    CeylonModelManager
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model.parsing {
    ProgressIndicatorMonitor
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    CeylonTokens
}

shared abstract class IdeaCompletionProvider()
        extends CompletionProvider<CompletionParameters>()  {
    
    shared formal CompletionOptions options;
    
    shared actual void addCompletions(CompletionParameters parameters,
        ProcessingContext context, variable CompletionResultSet result) {
        assert (exists project = parameters.editor.project,
                exists modelManager = getModelManager(project));
        try {
            modelManager.pauseAutomaticModelUpdate();
        
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
                    //TODO: figure out the doc link prefix
                    result = result.withPrefixMatcher("");
                }
                if (position.elementType == CeylonTokens.pidentifier) {
                    // In case of a package identifier like `ceylon.collection`, we compute a reference
                    // on the whole path, which will lead IntelliJ to create prefixes like `ceylon.col`
                    // whereas completionManager will return things like `collection`, which won't match
                    // the prefix. We thus have to change the prefix to what's after the dot.
                    String text = position.text;
                    value loc = text.firstInclusion(CompletionInitializationContext.dummyIdentifierTrimmed);
                    String prefix = if (exists loc) then text[0:loc] else text;
                    result = result.withPrefixMatcher(prefix);
                }
            }
            
            value progressMonitor = ProgressIndicatorMonitor.wrap {
                object monitor extends EmptyProgressIndicator() {
                    // hashCode() seems to be quite slow when used in CoreProgressManager.threadsUnderIndicator
                    hash => 43;
                    
                }
            };
            object listener extends ApplicationAdapter() {
                shared actual void beforeWriteActionStart(Object action) {
                    if (!progressMonitor.cancelled) {
                        progressMonitor.wrapped.cancel();
                    }
                }
            }
            if (! application.writeActionPending) {
                application.addApplicationListener(listener);
                try {
                    concurrencyManager.withAlternateResolution(() {
                        if (is CeylonFile ceylonFile = parameters.originalFile,
                            exists localAnalyzer = ceylonFile.localAnalyzer) {
                            LocalAnalysisResult? analysisResult;
                            if (! modelManager.busy) {
                                if (parameters.autoPopup) {
                                    analysisResult = localAnalyzer.ensureTypechecked(progressMonitor, 0);
                                } else {
                                    analysisResult = localAnalyzer.ensureTypechecked(progressMonitor, 4);
                                }
                            } else {
                                if (parameters.autoPopup) {
                                    analysisResult = null;
                                } else {
                                    if (exists result = localAnalyzer.result,
                                        exists lastTypecheckedCU = result.lastCompilationUnit) {
                                        analysisResult = result;
                                        completionServiceImpl.currentCompletion?.addAdvertisement(
                                            "The results might be incomplete during a Ceylon model update",
                                            MessageType.warning.popupBackground);
                                    } else {
                                        analysisResult = null;
                                    }
                                }
                            }
                            if (exists analysisResult) {
                                addCompletionsInternal {
                                    modelManager = modelManager;
                                    parameters = parameters;
                                    context = context;
                                    result = result;
                                    analysisResult = analysisResult;
                                    options = options;
                                    progressMonitor = progressMonitor;
                                };
                            }
                        }
                    });
                } catch (ProcessCanceledException e) {
                    noop();// for debugging purposes
                } finally {
                    application.removeApplicationListener(listener);
                }
            }
        } finally {
            modelManager.resumeAutomaticModelUpdate();
        }
    }
    
    void addCompletionsInternal(CeylonModelManager modelManager,
        CompletionParameters parameters,
        ProcessingContext context, CompletionResultSet result,
        LocalAnalysisResult analysisResult, CompletionOptions options,
        ProgressIndicatorMonitor progressMonitor) {
        
        if (exists element = parameters.originalPosition) {
            assert (is CeylonFile ceylonFile = element.containingFile,
                    exists project = parameters.editor.project);

            value isSecondLevel
                    = parameters.invocationCount > 0
                    && parameters.invocationCount % 2 == 0;

            value params = IdeaCompletionContext {
                file = ceylonFile;
                localAnalysisResult = analysisResult;
                editor = parameters.editor;
                options = options;
                result = result;
            };
            value doc = parameters.editor.document;
            completionManager.getContentProposals {
                typecheckedRootNode = params.lastCompilationUnit;
                ctx = params;
                offset = parameters.editor.caretModel.offset;
                line = doc.getLineNumber(element.textOffset);
                secondLevel = isSecondLevel;
                monitor = progressMonitor;
                // The parameters tooltip has nothing to do with code completion, so we bypass it
                returnedParamInfo = true;
            };

            installCustomLookupCellRenderer(project);

            if (!isSecondLevel) {
                if (!modelManager.busy) {
                    result.addLookupAdvertisement("Call again to toggle second-level completions");
                }
            }
        }
    }
}
