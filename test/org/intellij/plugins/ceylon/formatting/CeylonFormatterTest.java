package org.intellij.plugins.ceylon.formatting;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.intellij.plugins.ceylon.psi.CeylonFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

/**
 * NOTE: this requires that the ceylon.formatter directory is a sibling directory to the ceylon-ide-intellij.
 */
public class CeylonFormatterTest extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "../ceylon.formatter/test-samples";
    }

    // pass:
    public void testFunctionTypes() throws Exception { testFormatting("functionTypes"); }
    public void testSpreadArguments() throws Exception { testFormatting("spreadArguments"); }
    public void testGroupedTypes() throws Exception { testFormatting("groupedTypes"); }
    public void testNot() throws Exception { testFormatting("not"); }
    public void testAttributeGetterDeclaration() throws Exception { testFormatting("attributeGetterDeclaration"); }
    public void testSwitch() throws Exception { testFormatting("switch"); }
    public void testBinaryOperators() throws Exception { testFormatting("binaryOperators"); }
    public void testOuter() throws Exception { testFormatting("outer"); }
    public void testIf() throws Exception { testFormatting("if"); }
    public void testTry() throws Exception { testFormatting("try"); }
    public void testBreak() throws Exception { testFormatting("break"); }
    public void testTuples() throws Exception { testFormatting("tuples"); }
    public void testCaseTypes() throws Exception { testFormatting("caseTypes"); }
    public void testPositiveNegativeOp() throws Exception { testFormatting("positiveNegativeOp"); }
    public void testTypeAliases() throws Exception { testFormatting("typeAliases"); }
    public void testIndexExpressions() throws Exception { testFormatting("indexExpressions"); }
    public void testPostfixOperators() throws Exception { testFormatting("postfixOperators"); }
    public void testTypeArguments() throws Exception { testFormatting("typeArguments"); }
    public void testComprehensions() throws Exception { testFormatting("comprehensions"); }
    public void testInterfaces() throws Exception { testFormatting("interfaces"); }
    public void testPrefixOperators() throws Exception { testFormatting("prefixOperators"); }
    public void testContinue() throws Exception { testFormatting("continue"); }
    public void testRangeOp() throws Exception { testFormatting("rangeOp"); }
    public void testTypes() throws Exception { testFormatting("types"); }
    public void testReturn() throws Exception { testFormatting("return"); }
    public void testWhile() throws Exception { testFormatting("while"); }
    public void testExistsNonempty() throws Exception { testFormatting("existsNonempty"); }
    public void testSelf() throws Exception { testFormatting("self"); }
    public void testWithinOps() throws Exception { testFormatting("withinOps"); }
    public void testExpressions() throws Exception { testFormatting("expressions"); }
    public void testSequencedArguments() throws Exception { testFormatting("sequencedArguments"); }
    public void testFor() throws Exception { testFormatting("for"); }
    public void testModule() throws Exception { testFormatting("module"); }
    public void testPackage() throws Exception { testFormatting("package"); }
    public void testQualifiedMemberOrTypeExpression() throws Exception { testFormatting("qualifiedMemberOrTypeExpressions"); }
    public void testTypeConstraints() throws Exception { testFormatting("typeConstraints"); }
    public void testAnnotationsNoArguments() throws Exception { testFormatting("annotationsNoArguments"); }

    // bugs
    public void testBug27() throws Exception { testFormatting("bugs/27"); }
    public void testBug30() throws Exception { testFormatting("bugs/30"); }

    ///////////////////////////////////////////////////////////////
    // only end-of-file WS handling etc.:
    public void testAssignments() throws Exception { testFormatting("assignments"); }
    public void testStringTemplates() throws Exception { testFormatting("stringTemplates"); }
    public void testHelloWorld() throws Exception { testFormatting("helloWorld"); }
    public void testHelloWorldCommented() throws Exception { testFormatting("helloWorldCommented"); }
    public void testObjectArguments() throws Exception { testFormatting("objectArguments"); }

    // adding redundant newlines after line comments:
    public void testThrow() throws Exception { testFormatting("throw"); }
    public void testCommentsAfterStatements() throws Exception { testFormatting("commentsAfterStatements"); }
    public void testSimpleClass() throws Exception { testFormatting("simpleClass"); }


    // ignoring options:
    public void testBraceOnOwnLine() throws Exception { testFormatting("braceOnOwnLine"); }
    public void testParamListParenWithSpaces() throws Exception { testFormatting("paramListParenWithSpaces"); }
    public void testParamListParenWithoutSpaces() throws Exception { testFormatting("paramListParenWithoutSpaces"); }
    public void testImportSingleLine() throws Exception { testFormatting("importSingleLine"); }
    public void testTypeOperatorExpressions() throws Exception { testFormatting("typeOperatorExpressions"); }

    // I don't agree (eg. =)
    public void testImportKeywords() throws Exception { testFormatting("importKeywords"); }


    ////////////////////////////////////////////////
    // fail:

    // newlines before RBRACE
    public void testSmallBlocks() throws Exception { testFormatting("smallBlocks"); }
    public void testIs() throws Exception { testFormatting("is"); }
    public void testObjects() throws Exception { testFormatting("objects"); }


    // other fail (todo in comments)
    public void testFunctionArguments() throws Exception { testFormatting("functionArguments"); } // no newline between functions; larger indent for anonymous function bodies
    public void testMultiLineStringIndented() throws Exception { testFormatting("multiLineStringIndented"); }  // indent multiline strings correctly when indenting the first line
    public void testMultiLineString() throws Exception { testFormatting("multiLineString"); } // indent multiline strings correctly when indenting the first line
    public void testAnnotationsPositionalArguments() throws Exception { testFormatting("annotationsPositionalArguments"); }  // fix IDE error
    public void testNamedArguments() throws Exception { testFormatting("namedArguments"); }  // fix indent for seq.args in named arg list; line comment newlines; single-line short block
    public void testImportMultiLine() throws Exception { testFormatting("importMultiLine"); } // single-line short block; = without spaces
    public void testDoc() throws Exception { testFormatting("doc"); } // doc annotation on separate line
    public void testLongInvocation() throws Exception { testFormatting("longInvocation"); }  // chop down long chained member invocations; line comment newlines
    public void testMemberOp() throws Exception { testFormatting("memberOp"); } // chop down long chained member selections
    public void testMultiLineParameterList() throws Exception { testFormatting("multiLineParameterList"); } // chop down long parameter lists, remove intra-param linebreaks
    public void testComments() throws Exception { testFormatting("comments"); } // format multi-line comments; don't add newlines after line comments


    private void testFormatting(String testSource) throws Exception {
        testSource = testSource + ".ceylon";
        final PsiFile[] files = myFixture.configureByFiles(testSource);
        final CeylonFile file = (CeylonFile) files[0];
        final File options = Paths.get(getTestDataPath(), testSource + ".options").toFile();
        if (options.exists()) {
            System.out.println("---- Options found: " + file.getName());
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(options)));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("--------");
        }

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override public void run() {
                CodeStyleManager.getInstance(getProject()).reformat(file);
            }
        });
        myFixture.checkResultByFile(testSource + ".formatted");
    }
}
