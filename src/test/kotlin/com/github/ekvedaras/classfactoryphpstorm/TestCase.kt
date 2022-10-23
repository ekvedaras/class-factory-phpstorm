package com.github.ekvedaras.classfactoryphpstorm

import com.intellij.codeInspection.InspectionProfileEntry
import com.intellij.testFramework.TestDataFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase

internal abstract class TestCase : BasePlatformTestCase() {
    protected fun assertCompletionContains(vararg shouldContain: String) {
        val strings = myFixture.lookupElementStrings ?: return fail("Empty completion result")

        assertContainsElements(strings, shouldContain.asList())
    }

    protected fun assertCompletionDoesNotContain(vararg shouldNotContain: String) {
        val strings = myFixture.lookupElementStrings ?: return

        assertDoesntContain(strings, shouldNotContain.asList())
    }

    protected fun assertInspection(
        @TestDataFile filePath: String,
        inspection: InspectionProfileEntry,
        inspectCaretFirst: Boolean = false
    ) {

        if (inspectCaretFirst) {
            myFixture.configureByFile(filePath)
            myFixture.elementAtCaret
            // The above is needed sometimes otherwise, PhpCache is empty
        }

        myFixture.enableInspections(inspection)
        myFixture.testHighlighting(filePath)
    }
}
