import com.intellij.codeInsight.lookup {
    LookupElement,
    LookupElementBuilder
}
import com.intellij.openapi.editor {
    Document,
    Editor
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.ide.common.correct {
    AssignToLocalQuickFix,
    AssignToLocalProposal
}
import com.redhat.ceylon.model.typechecker.model {
    Type,
    Unit,
    Declaration
}

import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    IdeaLinkedModeSupport,
    IdeaLinkedMode
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}
import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
}
import com.intellij.util.containers {
    HashSet
}

object ideaAssignToLocalQuickFix
        satisfies AssignToLocalQuickFix<CeylonFile,Module,IdeaQuickFixData>
                & IdeaDocumentChanges
                & IdeaQuickFix {

    shared actual void newProposal(IdeaQuickFixData data, String desc) {
        data.registerFix { 
            desc = desc;
            change = null;
            callback = (p, e, f) {
                if(is CeylonFile f) {
                    AssignToLocalElement(data, p, e, f).perform();
                }
            };
        };
    }
}

class AssignToLocalElement(IdeaQuickFixData data, Project p, Editor e, CeylonFile f)
        satisfies AssignToLocalProposal<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement,IdeaLinkedMode>
                & IdeaQuickFix
                & IdeaDocumentChanges
                & IdeaLinkedModeSupport {
    
    shared actual variable Integer currentOffset = e.caretModel.offset;
    
    shared actual variable Integer exitPos = 0;
    
    shared actual variable {String*} names = empty;
    
    shared actual variable Integer offset = 0;
    
    shared actual variable Type? type = null;
    
    shared void perform() {
        if (exists change = performInitialChange(data, f, currentOffset)) {
            change.apply(p);
        }
        if (exists lm = addLinkedPositions(e.document, f.phasedUnit.unit)) {
            installLinkedMode(e.document, lm, this, 0, 0);
        }
    }
    
    shared actual LookupElement[] toProposals(<String|Type>[] types, 
        Integer offset, Unit unit) => types.map((type) {
            if (is String type) {
                return LookupElementBuilder.create(type).withIcon(ideaIcons.correction);
            }
            return LookupElementBuilder.create(type.asString(unit))
                    .withIcon(ideaIcons.forDeclaration(type.declaration))
                    .withInsertHandler(object satisfies InsertHandler<LookupElement> {
                        shared actual void handleInsert(InsertionContext ctx, LookupElement el) {
                            // TODO abstract that
                            value imports = HashSet<Declaration>();
                            ideaImportProposals.importType(imports, type, data.rootNode);
                            if (!imports.empty) {
                                value change = newTextChange("Import type", data.doc);
                                initMultiEditChange(change);
                                ideaImportProposals.applyImports(change, imports, data.rootNode, data.doc);
                                change.apply(ctx.project);
                            }
                        }
                    });
        }).sequence();

    shared actual LookupElement[] toNameProposals(String[] names, Integer offset, Unit unit, Integer seq) =>
            names.map(
                (n) => LookupElementBuilder.create(n).withIcon(ideaIcons.local)
            ).sequence();
}
