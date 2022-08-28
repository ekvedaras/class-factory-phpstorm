package com.github.ekvedaras.classfactoryphpstorm

import com.intellij.codeInspection.InspectionProfileEntry
import com.intellij.testFramework.TestDataFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

internal abstract class TestCase : BasePlatformTestCase() {
    protected fun assertCompletionContains(vararg shouldContain: String) {
        val strings = myFixture.lookupElementStrings ?: return fail("Empty completion result")

        assertContainsElements(strings, shouldContain.asList())
    }

    protected fun assertCompletionDoesNotContain(vararg shouldNotContain: String) {
        val strings = myFixture.lookupElementStrings ?: return

        assertDoesntContain(strings, shouldNotContain.asList())
    }

    protected fun assertInspection(@TestDataFile filePath: String, inspection: InspectionProfileEntry) {
        myFixture.enableInspections(inspection)

//         Delay is required otherwise tests randomly fail due to PSI tree changes during highlighting 🤷‍
//        runBlocking { delay(500L) }

        myFixture.testHighlighting(filePath)
    }
}
