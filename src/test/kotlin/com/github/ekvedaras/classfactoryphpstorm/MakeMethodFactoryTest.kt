package com.github.ekvedaras.classfactoryphpstorm

import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.IncorrectPropertyTypeInspection
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.IncorrectPropertyTypeInspectionForClosureReturns
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.IncorrectPropertyTypeInspectionInInDirectlyPassedClosureReturnedArray
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.PropertyNotFoundInspection
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.PropertyNotFoundInspectionInArrayKeysInDirectlyPassedClosure
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.PropertyNotFoundInspectionInAttributesArrayKeys
import com.jetbrains.php.lang.inspections.PhpInspection

internal class MakeMethodFactoryTest : EssentialTestCase() {
    override fun getTestDataPath() = "src/test/testData/make"
    override fun propertyNotFoundInspection(): PhpInspection = PropertyNotFoundInspection()
    override fun propertyNotFoundInAttributesArrayInspection(): PhpInspection =
        PropertyNotFoundInspectionInAttributesArrayKeys()

    override fun propertyNotFoundInArrayKeysInDirectlyPassedClosure(): PhpInspection =
        PropertyNotFoundInspectionInArrayKeysInDirectlyPassedClosure()

    override fun incorrectPropertyTypeInspection(): PhpInspection = IncorrectPropertyTypeInspection()
    override fun incorrectPropertyTypeInClosureReturnsInspection(): PhpInspection = IncorrectPropertyTypeInspectionForClosureReturns()
    override fun incorrectPropertyTypeInDirectlyPassedClosureReturnedArrayValues(): PhpInspection = IncorrectPropertyTypeInspectionInInDirectlyPassedClosureReturnedArray()

    override fun testItResolvesReferencesInArrayReturnedByDirectlyPassedClosure() {
        // TODO: failing due to PhpCache having cached empty results. Find a workaround
    }
}