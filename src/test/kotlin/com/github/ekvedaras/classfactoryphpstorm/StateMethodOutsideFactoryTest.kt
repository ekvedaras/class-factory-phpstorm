package com.github.ekvedaras.classfactoryphpstorm

import com.jetbrains.php.lang.inspections.PhpInspection

internal class StateMethodOutsideFactoryTest : EssentialTestCase() {
    override fun getTestDataPath() = "src/test/testData/state/outsideFactory"
    override fun propertyNotFoundInspection(): PhpInspection = PropertyNotFoundInspection()
    override fun propertyNotFoundInAttributesArrayInspection(): PhpInspection =
        PropertyNotFoundInspectionInAttributesArrayKeys()
}