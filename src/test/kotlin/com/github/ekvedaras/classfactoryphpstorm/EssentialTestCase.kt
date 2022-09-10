package com.github.ekvedaras.classfactoryphpstorm

import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.jetbrains.php.lang.inspections.PhpInspection

internal abstract class EssentialTestCase : TestCase() {
    abstract fun propertyNotFoundInspection(): PhpInspection
    abstract fun propertyNotFoundInAttributesArrayInspection(): PhpInspection

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

    fun testItCompletesPropertiesAsArrayKeysOfAttributesArrayInClosure() {
        myFixture.configureByFile("essential/caretAtArrayKeyOfAttributesInClosure.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "age")
    }

    open fun testItCompletesPropertiesAsArrayKeysOfAttributesArrayInDirectlyPassedClosure() {
        myFixture.configureByFile("essential/caretAtArrayKeyOfAttributesInDirectlyPassedClosure.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "age")
    }

    fun testItCompletesPropertiesAsArrayKeysOfAttributesArrayInShortClosure() {
        myFixture.configureByFile("essential/caretAtArrayKeyOfAttributesInShortClosure.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "age")
    }

    fun testItCompletesObjectPropertiesForAttributesArrayInShortClosure() {
        myFixture.configureByFile("essential/caretAtArrayKeyMethodCallOfAttributesInShortClosure.php")
        myFixture.completeBasic()

        assertCompletionContains("value")
    }

    open fun testItCompletesObjectPropertiesForAttributesArrayInDirectlyPassedShortClosure() {
        myFixture.configureByFile("essential/caretAtArrayKeyMethodCallOfAttributesInDirectlyPassedShortClosure.php")
        myFixture.completeBasic()

        assertCompletionContains("value")
    }

    fun testItDoesNotCompleteAndDoesNotCrashWhenClosureHasNoParameters() {
        myFixture.configureByFile("essential/caretAtArrayKeyInClosureWithoutParameters.php")
        myFixture.completeBasic()

        assertCompletionDoesNotContain("id", "age")
    }

    fun testItDoesNotCompleteAndDoesNotCrashWhileResolveTypeWhenShortClosureHasNoParameters() {
        myFixture.configureByFile("essential/caretAtArrayKeyMethodCallInShortClosureWithoutParameters.php")
        myFixture.completeBasic()

        assertCompletionDoesNotContain("value")
    }

//    These tests require a workaround in build.gradle.kts due to a bug in intellij plugin. See https://github.com/JetBrains/gradle-intellij-plugin/issues/1094

    fun testItReportsNotFoundProperties() {
        assertInspection("essential/nonExistingProperty.php", propertyNotFoundInspection())
    }

    fun testItReportsNotFoundPropertiesInAttributesArray() {
        myFixture.configureByFile("essential/nonExistingPropertyInAttributesArray.php")
        myFixture.elementAtCaret
        // The above is needed otherwise, PhpCache is empty for \AccountFactory 🤷‍♂️

        assertInspection(
            "essential/nonExistingPropertyInAttributesArray.php",
            propertyNotFoundInAttributesArrayInspection()
        )
    }

//    fun testItResolvesReferencesInAssociativeArrayKeys() {
//        val usages = myFixture.testFindUsagesUsingAction("filledDefinition.php")
//        assertEquals(1, usages.size)
//    }
}