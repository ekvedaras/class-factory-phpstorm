package com.github.ekvedaras.classfactoryphpstorm

import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection.IncorrectPropertyTypeInspectionForClosureReturnsInDefinitionMethod
import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection.IncorrectPropertyTypeInspectionInDefinitionMethod
import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection.MissingClassPropertiesDefinitions
import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection.PropertyNotFoundInspectionInAttributesArrayKeysInDefinitionMethod
import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection.PropertyNotFoundInspectionInDefinitionMethod
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.PropertyNotFoundInspectionInArrayKeysInDirectlyPassedClosure
import com.github.ekvedaras.classfactoryphpstorm.support.ClassPropertyReference
import com.intellij.usages.ReadWriteAccessUsageInfo2UsageAdapter
import com.jetbrains.php.lang.inspections.PhpInspection

internal class DefinitionMethodTest : EssentialTestCase() {
    override fun getTestDataPath() = "src/test/testData/definition"

    override fun propertyNotFoundInspection(): PhpInspection =
        PropertyNotFoundInspectionInDefinitionMethod()

    override fun propertyNotFoundInAttributesArrayInspection(): PhpInspection =
        PropertyNotFoundInspectionInAttributesArrayKeysInDefinitionMethod()

    override fun incorrectPropertyTypeInspection(): PhpInspection =
        IncorrectPropertyTypeInspectionInDefinitionMethod()

    override fun incorrectPropertyTypeInClosureReturnsInspection(): PhpInspection =
        IncorrectPropertyTypeInspectionForClosureReturnsInDefinitionMethod()


    override fun propertyNotFoundInArrayKeysInDirectlyPassedClosure(): PhpInspection =
        PropertyNotFoundInspectionInArrayKeysInDirectlyPassedClosure()

    override fun incorrectPropertyTypeInDirectlyPassedClosureReturnedArrayValues(): PhpInspection =
        PropertyNotFoundInspectionInArrayKeysInDirectlyPassedClosure()

    fun testItDetectsMissingPropertyDefinitions() {
        assertInspection("missingPropertyDefinition.php", MissingClassPropertiesDefinitions())
    }

    fun testItUnderstandsStringArrayVsArray() {
        assertInspection("stringArray.php", this.incorrectPropertyTypeInspection())
    }

    fun testItUnderstandsBooleanVsFalse() {
        assertInspection("boolean.php", this.incorrectPropertyTypeInspection())
    }

    override fun testItResolvesReferencesInAssociativeArrayKeys() {
        val usages = myFixture.testFindUsagesUsingAction("essential/caretAtIdInConstructorWithUsage.php")

        assertEquals(1, usages.size)

        val usage = usages.first() as ReadWriteAccessUsageInfo2UsageAdapter

        assertEquals(ClassPropertyReference::class.java, usage.referenceClass)
        assertTrue(usage.element?.textMatches("'id'") ?: false)
        assertTrue(usage.element?.textRange?.startOffset == usage.navigationRange.startOffset - 1)
        assertTrue(usage.element?.textRange?.endOffset == usage.navigationRange.endOffset + 1)
    }

    override fun testItResolvesReferencesInAssociativeArrayKeysWhenThereIsNoConstructor() {
        val usages = myFixture.testFindUsagesUsingAction("essential/caretAtIdPropertyWithUsage.php")

        assertEquals(1, usages.size)

        val usage = usages.first() as ReadWriteAccessUsageInfo2UsageAdapter

        assertEquals(ClassPropertyReference::class.java, usage.referenceClass)
        assertTrue(usage.element?.textMatches("'id'") ?: false)
        assertTrue(usage.element?.textRange?.startOffset == usage.navigationRange.startOffset - 1)
        assertTrue(usage.element?.textRange?.endOffset == usage.navigationRange.endOffset + 1)
    }

    override fun testItCompletesPropertiesAsArrayKeysOfAttributesArrayInDirectlyPassedClosure() {
        // Not relevant
    }

    override fun testItCompletesObjectPropertiesForAttributesArrayInDirectlyPassedShortClosure() {
        // Not relevant
    }

    override fun testItCompletesPropertiesAsArrayKeysArrayInDirectlyPassedClosure() {
        // Not relevant
    }

    override fun testItReportsNotFoundPropertiesAsArrayKeysArrayInDirectlyPassedClosure() {
        // Not relevant
    }

    override fun testItResolvesReferencesInArrayReturnedByDirectlyPassedClosure() {
        // Not relevant
    }

    override fun testItReportsIncorrectPropertyTypesDirectlyPassedClosureReturnedArrayValues() {
        // Not relevant
    }

    override fun testItReportsIncorrectPropertyTypesDirectlyPassedClosureReturnedArrayValuesWithFactories() {
        // Not relevant
    }
}