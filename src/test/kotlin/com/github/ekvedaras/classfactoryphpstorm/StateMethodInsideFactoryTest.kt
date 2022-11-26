package com.github.ekvedaras.classfactoryphpstorm

import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.IncorrectPropertyTypeInspection
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.IncorrectPropertyTypeInspectionForClosureReturns
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.IncorrectPropertyTypeInspectionInInDirectlyPassedClosureReturnedArray
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.PropertyNotFoundInspection
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.PropertyNotFoundInspectionInArrayKeysInDirectlyPassedClosure
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.PropertyNotFoundInspectionInAttributesArrayKeys
import com.jetbrains.php.lang.inspections.PhpInspection

internal class StateMethodInsideFactoryTest : EssentialTestCase() {
    override fun getTestDataPath() = "src/test/testData/state/insideFactory"
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

    fun testItCompletesAttributesInStateOfAccountFactory() {
        myFixture.configureByFile("accountFactoryCaretAtAttributesArrayKey.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "exists", "accountReference", "name", "tradingName", "currency", "ratingInputs")
    }

    fun testNestedStateCalls() {
        assertInspection("nestedStateCalls.php", this.incorrectPropertyTypeInspection())
        assertInspection("nestedStateCalls.php", this.incorrectPropertyTypeInClosureReturnsInspection())
        assertInspection("nestedStateCalls.php", this.incorrectPropertyTypeInDirectlyPassedClosureReturnedArrayValues())
        assertInspection("nestedStateCalls.php", this.propertyNotFoundInspection())
        assertInspection("nestedStateCalls.php", this.propertyNotFoundInAttributesArrayInspection())
        assertInspection("nestedStateCalls.php", this.propertyNotFoundInArrayKeysInDirectlyPassedClosure())
    }
}