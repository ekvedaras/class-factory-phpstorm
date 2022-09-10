package com.github.ekvedaras.classfactoryphpstorm

import com.github.ekvedaras.classfactoryphpstorm.outsideClassFactory.make.PropertyNotFoundInspectionInAttributesArrayKeysInMakeMethod
import com.jetbrains.php.lang.inspections.PhpInspection

internal class MakeMethodFactoryTest : EssentialTestCase() {
    override fun getTestDataPath() = "src/test/testData/make"
    override fun propertyNotFoundInspection(): PhpInspection = PropertyNotFoundInspection()
    override fun propertyNotFoundInAttributesArrayInspection(): PhpInspection =
        PropertyNotFoundInspectionInAttributesArrayKeysInMakeMethod()
}