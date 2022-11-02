package com.github.ekvedaras.classfactoryphpstorm

import com.github.ekvedaras.classfactoryphpstorm.support.ClassPropertyReference
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.usages.ReadWriteAccessUsageInfo2UsageAdapter
import com.jetbrains.php.lang.inspections.PhpInspection

internal abstract class EssentialTestCase : TestCase() {
    abstract fun propertyNotFoundInspection(): PhpInspection
    abstract fun propertyNotFoundInAttributesArrayInspection(): PhpInspection
    abstract fun propertyNotFoundInArrayKeysInDirectlyPassedClosure(): PhpInspection

    abstract fun incorrectPropertyTypeInspection(): PhpInspection
    abstract fun incorrectPropertyTypeInClosureReturnsInspection(): PhpInspection
    abstract fun incorrectPropertyTypeInDirectlyPassedClosureReturnedArrayValues(): PhpInspection

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

    fun testItCompletesClassPropertiesInAssociativeArrayKeyWhenThereIsNoConstructor() {
        myFixture.configureByFile("essential/caretAtAssociativeArrayKeyWhenThereIsNoConstructor.php")
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

    open fun testItCompletesPropertiesAsArrayKeysArrayInDirectlyPassedClosure() {
        myFixture.configureByFile("essential/caretAtArrayKeyInDirectlyPassedShortClosure.php")
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

    fun testItCompletesObjectPropertiesForAttributesArrayInShortClosureWhenAttributeDefinitionIsAlsoAClosure() {
        myFixture.configureByFile("essential/caretAtArrayKeyMethodCallOfAttributesInShortClosureWhenTypeIsAlsoAClosure.php")
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

    // These tests below require a workaround in build.gradle.kts due to a bug in intellij plugin. See https://github.com/JetBrains/gradle-intellij-plugin/issues/1094

    fun testItReportsNotFoundProperties() {
        assertInspection("essential/nonExistingProperty.php", propertyNotFoundInspection())
    }

    fun testItReportsNotFoundPropertiesWhenThereIsNoConstructor() {
        assertInspection("essential/nonExistingPropertyWhenThereIsNoConstructor.php", propertyNotFoundInspection())
    }

    fun testItReportsNotFoundPropertiesInAttributesArray() {
        assertInspection(
            "essential/nonExistingPropertyInAttributesArray.php",
            propertyNotFoundInAttributesArrayInspection(),
        )
    }

    open fun testItReportsNotFoundPropertiesAsArrayKeysArrayInDirectlyPassedClosure() {
        assertInspection(
            "essential/nonExistingPropertyInReturnedArrayOfDirectlyPassedClosure.php",
            propertyNotFoundInArrayKeysInDirectlyPassedClosure()
        )
    }

    fun testItReportsIncorrectPropertyTypes() {
        assertInspection(
            "essential/propertyWithWrongType.php",
            incorrectPropertyTypeInspection(),
        )
    }

    fun testItCanHandleClosureValues() {
        assertInspection(
            "essential/propertyWithClosureValue.php",
            incorrectPropertyTypeInspection(),
        )
    }

    fun testItReportsIncorrectPropertyTypesWhenThereIsNoConstructor() {
        assertInspection(
            "essential/propertyWithWrongTypeWhenThereIsNoConstructor.php",
            incorrectPropertyTypeInspection(),
        )
    }

    fun testItReportsIncorrectPropertyTypesInClosureReturns() {
        assertInspection(
            "essential/propertyWithWrongTypeInClosureReturn.php",
            incorrectPropertyTypeInClosureReturnsInspection(),
        )
    }

    fun testItReportsIncorrectPropertyTypesWhenReturnedByClosure() {
        assertInspection(
            "essential/propertyWithWrongTypeReturnedByClosure.php",
            incorrectPropertyTypeInClosureReturnsInspection(),
        )
    }

    fun testItReportsIncorrectPropertyTypesInClosureReturnsWithFactories() {
        assertInspection(
            "essential/propertyWithWrongTypeInClosureReturnWithFactories.php",
            incorrectPropertyTypeInClosureReturnsInspection(),
        )
    }

    open fun testItReportsIncorrectPropertyTypesDirectlyPassedClosureReturnedArrayValues() {
        assertInspection(
            "essential/propertyWithWrongTypeInReturnedArrayOfDirectlyPassedClosure.php",
            incorrectPropertyTypeInDirectlyPassedClosureReturnedArrayValues(),
        )
    }

    open fun testItReportsIncorrectPropertyTypesDirectlyPassedClosureReturnedArrayValuesWithFactories() {
        assertInspection(
            "essential/propertyWithWrongTypeInReturnedArrayOfDirectlyPassedClosureWithFactories.php",
            incorrectPropertyTypeInDirectlyPassedClosureReturnedArrayValues(),
        )
    }

    fun testItReportsIncorrectPropertyTypesWithFactories() {
        assertInspection(
            "essential/propertyWithWrongTypeWithFactories.php",
            incorrectPropertyTypeInspection(),
        )
    }

    open fun testItResolvesReferencesInAssociativeArrayKeys() {
        val usages = myFixture.testFindUsagesUsingAction("essential/caretAtIdInConstructorWithUsage.php")

        assertEquals(2, usages.size)

        val usage = usages.first() as ReadWriteAccessUsageInfo2UsageAdapter

        assertEquals(ClassPropertyReference::class.java, usage.referenceClass)
        assertTrue(usage.element?.textMatches("'id'") ?: false)
        assertTrue(usage.element?.textRange?.startOffset == usage.navigationRange.startOffset - 1)
        assertTrue(usage.element?.textRange?.endOffset == usage.navigationRange.endOffset + 1)
    }

    open fun testItResolvesReferencesInAssociativeArrayKeysWhenThereIsNoConstructor() {
        val usages = myFixture.testFindUsagesUsingAction("essential/caretAtIdPropertyWithUsage.php")

        assertEquals(2, usages.size)

        val usage = usages.first() as ReadWriteAccessUsageInfo2UsageAdapter

        assertEquals(ClassPropertyReference::class.java, usage.referenceClass)
        assertTrue(usage.element?.textMatches("'id'") ?: false)
        assertTrue(usage.element?.textRange?.startOffset == usage.navigationRange.startOffset - 1)
        assertTrue(usage.element?.textRange?.endOffset == usage.navigationRange.endOffset + 1)
    }

    fun testItResolvesReferencesInAttributesArrayKeys() {
        val usages =
            myFixture.testFindUsagesUsingAction("essential/caretAtIdInConstructorWithUsageInAttributesArray.php")

        assertEquals(2, usages.size)

        usages.map { it as ReadWriteAccessUsageInfo2UsageAdapter }.forEach { usage ->
            assertEquals(ClassPropertyReference::class.java, usage.referenceClass)
            assertTrue(usage.element?.textMatches("'id'") ?: false)
            assertTrue(usage.element?.textRange?.startOffset == usage.navigationRange.startOffset - 1)
            assertTrue(usage.element?.textRange?.endOffset == usage.navigationRange.endOffset + 1)
        }
    }

    open fun testItResolvesReferencesInArrayReturnedByDirectlyPassedClosure() {
        val usages =
            myFixture.testFindUsagesUsingAction("essential/caretAtAgeInConstructorWithUsageInArrayReturnedByDirectlyPassedClosure.php")

        assertEquals(2, usages.size)

        usages.map { it as ReadWriteAccessUsageInfo2UsageAdapter }.forEach { usage ->
            assertEquals(ClassPropertyReference::class.java, usage.referenceClass)
            assertTrue(usage.element?.textMatches("'age'") ?: false)
            assertTrue(usage.element?.textRange?.startOffset == usage.navigationRange.startOffset - 1)
            assertTrue(usage.element?.textRange?.endOffset == usage.navigationRange.endOffset + 1)
        }
    }
}