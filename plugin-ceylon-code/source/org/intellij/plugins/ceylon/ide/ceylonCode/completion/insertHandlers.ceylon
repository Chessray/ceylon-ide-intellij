import ceylon.interop.java {
    javaString
}

import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
}
import com.intellij.codeInsight.completion.util {
    ParenthesesInsertHandler
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.extensions {
    Extensions
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Unit,
    Function
}

object declarationInsertHandler satisfies InsertHandler<LookupElement> {
    
    shared actual void handleInsert(InsertionContext ctx, LookupElement elem) {
        if (is [Declaration, Unit] seq = elem.\iobject) {
            value importHandler = Extensions.getExtensions(importHandlerEpName).get(0);
            value decl = seq.first;
            value unit = seq[1];
            
            if (!exists im = unit.getImport(unit.getAliasedName(decl)),
                !decl.unit.\ipackage.nameAsString.equals("ceylon.language")) {
                
                importHandler.importDeclaration(decl, ctx);
            }
        } else {
            print(elem.\iobject);
        }
    }
}

object functionInsertHandler extends ParenthesesInsertHandler<LookupElement>() {
    
    shared actual void handleInsert(InsertionContext ctx, LookupElement elem) {
        super.handleInsert(ctx, elem);

        if (is [Function, Unit] seq = elem.\iobject, seq.first.toplevel) {
            declarationInsertHandler.handleInsert(ctx, elem);
        }
    }

    shared actual Boolean placeCaretInsideParentheses(InsertionContext ctx, LookupElement elem) {
        return if (is [Function, Unit] seq = elem.\iobject, seq.first.firstParameterList.parameters.size() > 0) then true else false;
    }
}

class ReplaceTextHandler(Integer startOffset, Integer stopOffset, String newText)
        satisfies InsertHandler<LookupElement> {
    
    shared actual void handleInsert(InsertionContext ctx, LookupElement elem) {
        ctx.document.replaceString(startOffset, ctx.editor.caretModel.offset, javaString(newText));
    }

}

object putCaretInBracesInsertHandler satisfies InsertHandler<LookupElement> {
    shared actual void handleInsert(InsertionContext ctx, LookupElement elem) {
        if (elem.lookupString.endsWith("{}")) {
            ctx.editor.caretModel.moveCaretRelatively(-1, 0, false, false, true);
        }
    }
}
