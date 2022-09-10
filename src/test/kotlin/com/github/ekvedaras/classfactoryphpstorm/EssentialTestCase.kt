package com.github.ekvedaras.classfactoryphpstorm

import com.github.ekvedaras.classfactoryphpstorm.insideClassFactory.definition.PropertyNotFoundInspectionInDefinitionMethod
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.PrioritizedLookupElement

internal abstract class EssentialTestCase : TestCase() {
    fun testItCompletesClassPropertiesInSimpleArray() {
        myFixture.configureByFile("essential/caretAtStringInSimpleArray.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "age")
    }

    fun testItCompletesClassPropertiesInAssociativeArrayKey() {
        myFixture.configureByFile("essential/caretAtAssociativeArrayKey.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "age")
    }

    fun testItDoesNotCompleteClassPropertiesInAssociativeArrayValue() {
        myFixture.configureByFile("essential/caretAtAssociativeArrayValue.php")
        myFixture.completeBasic()

        assertTrue(myFixture.lookupElements?.isEmpty() ?: true)
    }

    fun testItCompletesPropertiesInOrderTheyAppearInConstructorAscending() {
        myFixture.configureByFile("essential/caretAtStringInSimpleArray.php")
        myFixture.completeBasic()

        assertEquals("id", myFixture.lookupElementStrings?.first())
        assertEquals(2.0, (myFixture.lookupElements?.first() as PrioritizedLookupElement<*>?)?.priority)
        assertEquals("age", myFixture.lookupElementStrings?.last())
        assertEquals(1.0, (myFixture.lookupElements?.last() as PrioritizedLookupElement<*>?)?.priority)
    }

    fun testItCompletesPropertiesInOrderTheyAppearInConstructorDescending() {
        myFixture.configureByFile("essential/caretAtStringInSimpleArrayButPropertiesSwapped.php")
        myFixture.completeBasic()

        assertEquals("age", myFixture.lookupElementStrings?.first())
        assertEquals(2.0, (myFixture.lookupElements?.first() as PrioritizedLookupElement<*>?)?.priority)
        assertEquals("id", myFixture.lookupElementStrings?.last())
        assertEquals(1.0, (myFixture.lookupElements?.last() as PrioritizedLookupElement<*>?)?.priority)
    }

    fun testItExcludesPropertiesThatAreAlreadyDefined() {
        myFixture.configureByFile("essential/caretAtStringInNewValueWhenOnePropertyIsAlreadyDefined.php")
        myFixture.completeBasic()

        assertCompletionDoesNotContain("id")
        assertCompletionContains("age")
    }

    fun testItCompletesNothingIfAllPropertiesAreAlreadyDefined() {
        myFixture.configureByFile("essential/caretAtStringInNewValueWhenAllPropertiesAreAlreadyDefined.php")
        myFixture.completeBasic()

        assertTrue(myFixture.lookupElements?.isEmpty() ?: true)
    }

    fun testItCompletesPropertiesAsArrayKeysOfAttributesArrayInClosure()
    {
        myFixture.configureByFile("essential/caretAtArrayKeyOfAttributesInClosure.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "age")
    }

    open fun testItCompletesPropertiesAsArrayKeysOfAttributesArrayInDirectlyPassedClosure()
    {
        myFixture.configureByFile("essential/caretAtArrayKeyOfAttributesInDirectlyPassedClosure.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "age")
    }

    fun testItCompletesPropertiesAsArrayKeysOfAttributesArrayInShortClosure()
    {
        myFixture.configureByFile("essential/caretAtArrayKeyOfAttributesInShortClosure.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "age")
    }

    fun testItCompletesObjectPropertiesForAttributesArrayInShortClosure()
    {
        myFixture.configureByFile("essential/caretAtArrayKeyMethodCallOfAttributesInShortClosure.php")
        myFixture.completeBasic()

        assertCompletionContains("value")
    }

    open fun testItCompletesObjectPropertiesForAttributesArrayInDirectlyPassedShortClosure()
    {
        myFixture.configureByFile("essential/caretAtArrayKeyMethodCallOfAttributesInDirectlyPassedShortClosure.php")
        myFixture.completeBasic()

        assertCompletionContains("value")
    }

    fun testItDoesNotCompleteAndDoesNotCrashWhenClosureHasNoParameters()
    {
        myFixture.configureByFile("essential/caretAtArrayKeyInClosureWithoutParameters.php")
        myFixture.completeBasic()

        assertCompletionDoesNotContain("id", "age")
    }

    fun testItDoesNotCompleteAndDoesNotCrashWhileResolveTypeWhenShortClosureHasNoParameters()
    {
        myFixture.configureByFile("essential/caretAtArrayKeyMethodCallInShortClosureWithoutParameters.php")
        myFixture.completeBasic()

        assertCompletionDoesNotContain("value")
    }

//    These tests don't work due to a big in intellij plugin. See https://github.com/JetBrains/gradle-intellij-plugin/issues/1094
//    See a workaround in build.gradle.kts

//    fun testItReportsNotFoundProperties() {
//        assertInspection("nonExistingProperty.php", PropertyNotFoundInspectionInDefinitionMethod())
//    }

//    fun testItResolvesReferencesInAssociativeArrayKeys() {
//        val usages = myFixture.testFindUsagesUsingAction("filledDefinition.php")
//        assertEquals(1, usages.size)
//    }
}