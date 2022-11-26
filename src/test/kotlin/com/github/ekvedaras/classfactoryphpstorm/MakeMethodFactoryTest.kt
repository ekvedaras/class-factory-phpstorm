package com.github.ekvedaras.classfactoryphpstorm

import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.IncorrectPropertyTypeInspection
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.IncorrectPropertyTypeInspectionForClosureReturns
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.IncorrectPropertyTypeInspectionInInDirectlyPassedClosureReturnedArray
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.PropertyNotFoundInspection
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.PropertyNotFoundInspectionInArrayKeysInDirectlyPassedClosure
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.PropertyNotFoundInspectionInAttributesArrayKeys
import com.github.ekvedaras.classfactoryphpstorm.support.ClassPropertyReference
import com.intellij.usages.ReadWriteAccessUsageInfo2UsageAdapter
import com.jetbrains.php.lang.inspections.PhpInspection

internal class MakeMethodFactoryTest : EssentialTestCase() {
    override fun getTestDataPath() = "src/test/testData/make"
    override fun propertyNotFoundInspection(): PhpInspection = PropertyNotFoundInspection()
    override fun propertyNotFoundInAttributesArrayInspection(): PhpInspection =
        PropertyNotFoundInspectionInAttributesArrayKeys()

    override fun propertyNotFoundInArrayKeysInDirectlyPassedClosure(): PhpInspection =
        PropertyNotFoundInspectionInArrayKeysInDirectlyPassedClosure()

    override fun incorrectPropertyTypeInspection(): PhpInspection = IncorrectPropertyTypeInspection()
    override fun incorrectPropertyTypeInClosureReturnsInspection(): PhpInspection =
        IncorrectPropertyTypeInspectionForClosureReturns()

    override fun incorrectPropertyTypeInDirectlyPassedClosureReturnedArrayValues(): PhpInspection =
        IncorrectPropertyTypeInspectionInInDirectlyPassedClosureReturnedArray()

    fun testItCanHandleMethodCallsOnOtherFactoriesFromAttributesArray() {
        assertInspection(
            "makeOtherFactoryFromAttributes.php",
            incorrectPropertyTypeInspection(),
        )

        assertInspection(
            "makeOtherFactoryFromAttributes.php",
            incorrectPropertyTypeInClosureReturnsInspection(),
        )
    }

    fun testItCanHandleNestedClosures() {
        assertInspection(
            "makeWithNestedClosures.php",
            incorrectPropertyTypeInClosureReturnsInspection(),
        )

//        TODO support bellow check
//        val usages =
//            myFixture.testFindUsagesUsingAction("makeWithNestedClosures.php")
//
//        assertEquals(2, usages.size)
//
//        usages.map { it as ReadWriteAccessUsageInfo2UsageAdapter }.forEach { usage ->
//            assertEquals(ClassPropertyReference::class.java, usage.referenceClass)
//            assertTrue(usage.element?.textMatches("'value'") ?: false)
//            assertTrue(usage.element?.textRange?.startOffset == usage.navigationRange.startOffset - 1)
//            assertTrue(usage.element?.textRange?.endOffset == usage.navigationRange.endOffset + 1)
//        }
    }
}