package com.github.ekvedaras.classfactoryphpstorm

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.PrioritizedLookupElement

internal abstract class EssentialTestCase : TestCase() {
    fun testItCompletesClassPropertiesInSimpleArray() {
        myFixture.configureByFile("caretAtStringInSimpleArray.php")
        myFixture.complete(CompletionType.BASIC)

        assertCompletionContains("id", "age")
    }

    fun testItCompletesClassPropertiesInAssociativeArrayKey() {
        myFixture.configureByFile("caretAtAssociativeArrayKey.php")
        myFixture.complete(CompletionType.BASIC)

        assertCompletionContains("id", "age")
    }

    fun testItDoesNotCompleteClassPropertiesInAssociativeArrayValue() {
        myFixture.configureByFile("caretAtAssociativeArrayValue.php")
        myFixture.complete(CompletionType.BASIC)

        assertTrue(myFixture.lookupElements?.isEmpty() ?: true)
    }

    fun testItCompletesPropertiesInOrderTheyAppearInConstructorAscending() {
        myFixture.configureByFile("caretAtStringInSimpleArray.php")
        myFixture.complete(CompletionType.BASIC)

        assertEquals("id", myFixture.lookupElementStrings?.first())
        assertEquals(2.0, (myFixture.lookupElements?.first() as PrioritizedLookupElement<*>?)?.priority)
        assertEquals("age", myFixture.lookupElementStrings?.last())
        assertEquals(1.0, (myFixture.lookupElements?.last() as PrioritizedLookupElement<*>?)?.priority)
    }

    fun testItCompletesPropertiesInOrderTheyAppearInConstructorDescending() {
        myFixture.configureByFile("caretAtStringInSimpleArrayButPropertiesSwapped.php")
        myFixture.complete(CompletionType.BASIC)

        assertEquals("age", myFixture.lookupElementStrings?.first())
        assertEquals(2.0, (myFixture.lookupElements?.first() as PrioritizedLookupElement<*>?)?.priority)
        assertEquals("id", myFixture.lookupElementStrings?.last())
        assertEquals(1.0, (myFixture.lookupElements?.last() as PrioritizedLookupElement<*>?)?.priority)
    }

    fun testItExcludesPropertiesThatAreAlreadyDefined() {
        myFixture.configureByFile("caretAtStringInNewValueWhenOnePropertyIsAlreadyDefined.php")
        myFixture.complete(CompletionType.BASIC)

        assertCompletionDoesNotContain("id")
        assertCompletionContains("age")
    }

    fun testItCompletesNorthingIfAllPropertiesAreAlreadyDefined() {
        myFixture.configureByFile("caretAtStringInNewValueWhenAllPropertiesAreAlreadyDefined.php")
        myFixture.complete(CompletionType.BASIC)

        assertTrue(myFixture.lookupElements?.isEmpty() ?: true)
    }

//    fun testItResolvesReferencesInAssociativeArrayKeys() {
//        val usages = myFixture.testFindUsagesUsingAction("filledDefinition.php")
//        assertEquals(1, usages.size)
//    }
}