import ceylon.collection {
    ArrayList
}

import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Editor
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.ide.common.completion {
    CompletionContext,
    ProposalsHolder
}
import com.redhat.ceylon.ide.common.model {
    BaseCeylonProject
}
import com.redhat.ceylon.ide.common.settings {
    CompletionOptions
}

import java.util.regex {
    Pattern
}

import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class IdeaCompletionContext(file, editor, ceylonProject, options) satisfies CompletionContext {
    shared Editor editor;
    shared CeylonFile file;
    
    shared actual BaseCeylonProject? ceylonProject;
    
    shared actual IdeaDocument commonDocument = IdeaDocument(editor.document);
    
    lastCompilationUnit => file.compilationUnit;
    parsedRootNode => file.compilationUnit;
    lastPhasedUnit => file.phasedUnit;
    tokens => file.tokens;
    typecheckedRootNode => file.compilationUnit;
    
    shared actual CompletionOptions options;
    
    shared actual List<Pattern> proposalFilters => empty;
    
    shared actual IdeaProposalsHolder proposals = IdeaProposalsHolder();
    
    shared actual TypeChecker typeChecker => ceylonProject?.typechecker else nothing;
}

shared class IdeaProposalsHolder() satisfies ProposalsHolder {
    value _proposals = ArrayList<LookupElement>();
    
    shared List<LookupElement> proposals => _proposals;
    
    size => _proposals.size;
    
    shared void add(LookupElement element) => _proposals.add(element);   
}
