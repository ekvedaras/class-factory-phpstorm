package com.github.ekvedaras.classfactoryphpstorm

import com.github.ekvedaras.classfactoryphpstorm.outsideClassFactory.state.PropertyNotFoundInspectionInAttributesArrayKeysInStateMethod
import com.jetbrains.php.lang.inspections.PhpInspection

internal class StateMethodOutsideFactoryTest : EssentialTestCase() {
    override fun getTestDataPath() = "src/test/testData/state/outsideFactory"
    override fun propertyNotFoundInspection(): PhpInspection = PropertyNotFoundInspection()
    override fun propertyNotFoundInAttributesArrayInspection(): PhpInspection =
        PropertyNotFoundInspectionInAttributesArrayKeysInStateMethod()
}