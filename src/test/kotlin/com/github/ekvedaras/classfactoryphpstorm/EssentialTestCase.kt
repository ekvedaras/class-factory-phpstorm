package com.github.ekvedaras.classfactoryphpstorm

import com.github.ekvedaras.classfactoryphpstorm.insideClassFactory.definition.PropertyNotFoundInspectionInDefinitionMethod
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.PrioritizedLookupElement

internal abstract class EssentialTestCase : TestCase() {
    fun testItCompletesClassPropertiesInSimpleArray() {
        myFixture.configureByFile("caretAtStringInSimpleArray.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "age")
    }

    fun testItCompletesClassPropertiesInAssociativeArrayKey() {
        myFixture.configureByFile("caretAtAssociativeArrayKey.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "age")
    }

    fun testItDoesNotCompleteClassPropertiesInAssociativeArrayValue() {
        myFixture.configureByFile("caretAtAssociativeArrayValue.php")
        myFixture.completeBasic()

        assertTrue(myFixture.lookupElements?.isEmpty() ?: true)
    }

    fun testItCompletesPropertiesInOrderTheyAppearInConstructorAscending() {
        myFixture.configureByFile("caretAtStringInSimpleArray.php")
        myFixture.completeBasic()

        assertEquals("id", myFixture.lookupElementStrings?.first())
        assertEquals(2.0, (myFixture.lookupElements?.first() as PrioritizedLookupElement<*>?)?.priority)
        assertEquals("age", myFixture.lookupElementStrings?.last())
        assertEquals(1.0, (myFixture.lookupElements?.last() as PrioritizedLookupElement<*>?)?.priority)
    }

    fun testItCompletesPropertiesInOrderTheyAppearInConstructorDescending() {
        myFixture.configureByFile("caretAtStringInSimpleArrayButPropertiesSwapped.php")
        myFixture.completeBasic()

        assertEquals("age", myFixture.lookupElementStrings?.first())
        assertEquals(2.0, (myFixture.lookupElements?.first() as PrioritizedLookupElement<*>?)?.priority)
        assertEquals("id", myFixture.lookupElementStrings?.last())
        assertEquals(1.0, (myFixture.lookupElements?.last() as PrioritizedLookupElement<*>?)?.priority)
    }

    fun testItExcludesPropertiesThatAreAlreadyDefined() {
        myFixture.configureByFile("caretAtStringInNewValueWhenOnePropertyIsAlreadyDefined.php")
        myFixture.completeBasic()

        assertCompletionDoesNotContain("id")
        assertCompletionContains("age")
    }

    fun testItCompletesNothingIfAllPropertiesAreAlreadyDefined() {
        myFixture.configureByFile("caretAtStringInNewValueWhenAllPropertiesAreAlreadyDefined.php")
        myFixture.completeBasic()

        assertTrue(myFixture.lookupElements?.isEmpty() ?: true)
    }

    fun testItCompletesPropertiesAsArrayKeysOfAttributesArrayInClosure()
    {
        myFixture.configureByFile("caretAtArrayKeyOfAttributesInClosure.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "age")
    }

    fun testItCompletesPropertiesAsArrayKeysOfAttributesArrayInShortClosure()
    {
        myFixture.configureByFile("caretAtArrayKeyOfAttributesInShortClosure.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "age")
    }

    fun testItCompletesObjectPropertiesForAttributesArrayInShortClosure()
    {
        myFixture.configureByFile("caretAtArrayKeyMethodCallOfAttributesInShortClosure.php")
        myFixture.completeBasic()

        assertCompletionContains("value")
    }

//    These tests don't work due to a big in intellij plugin. See https://github.com/JetBrains/gradle-intellij-plugin/issues/1094

//    fun testItReportsNotFoundProperties() {
//        assertInspection("nonExistingProperty.php", PropertyNotFoundInspectionInDefinitionMethod())
//    }

//    fun testItResolvesReferencesInAssociativeArrayKeys() {
//        val usages = myFixture.testFindUsagesUsingAction("filledDefinition.php")
//        assertEquals(1, usages.size)
//    }
}