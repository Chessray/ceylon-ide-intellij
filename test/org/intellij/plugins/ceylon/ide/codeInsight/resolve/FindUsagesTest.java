package org.intellij.plugins.ceylon.ide.codeInsight.resolve;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.usageView.UsageInfo;
import org.intellij.plugins.ceylon.ide.CeylonCodeInsightTestSupport;

import java.util.Collection;

public class FindUsagesTest extends CeylonCodeInsightTestSupport {

    public void testFindUsages() throws Exception {
        initFile("referenceTest.ceylon");
        final Editor editor = myFixture.getEditor();
        assertUsages(editor, 100, 3, "myHelloWorld");
        assertUsages(editor, 164, 3, "prout");
        assertUsages(editor, 183, 3, "bar2");
        assertUsages(editor, 422, 3, "BarBar");
        assertUsages(editor, 366, 2, "plop");
        assertUsages(editor, 473, 2, "plop");
        assertUsages(editor, 292, 3, "myHelloWorld");
        assertUsages(editor, 545, 2, "par");
        assertUsages(editor, 563, 2, "par");
    }

    private void assertUsages(Editor editor, int offset, int expectedCount, String identifier) {
        PsiElement targetElement = TargetElementUtilBase.getInstance().findTargetElement(
                editor,
                TargetElementUtilBase.ELEMENT_NAME_ACCEPTED | TargetElementUtilBase.REFERENCED_ELEMENT_ACCEPTED,
                offset);
        assertNotNull("Cannot find referenced element", targetElement);
        assertTrue(targetElement + " has no name identifier", (targetElement instanceof PsiNameIdentifierOwner));
        final PsiNameIdentifierOwner element = (PsiNameIdentifierOwner) targetElement;
        assertEquals(identifier, element.getNameIdentifier().getText());

        final Collection<UsageInfo> usageInfos = myFixture.findUsages(targetElement);
        assertEquals(expectedCount, usageInfos.size());
        for (UsageInfo usageInfo : usageInfos) {
            assertTrue(usageInfo.getElement().getText().contains(identifier));
            assertEquals(targetElement, usageInfo.getReference().resolve());
        }
    }
}
