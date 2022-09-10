package com.github.ekvedaras.classfactoryphpstorm

import com.jetbrains.php.lang.inspections.PhpInspection

internal class MakeMethodFactoryTest : EssentialTestCase() {
    override fun getTestDataPath() = "src/test/testData/make"
    override fun propertyNotFoundInspection(): PhpInspection = PropertyNotFoundInspection()
    override fun propertyNotFoundInAttributesArrayInspection(): PhpInspection =
        PropertyNotFoundInspectionInAttributesArrayKeys()
}