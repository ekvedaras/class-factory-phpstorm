package com.github.ekvedaras.classfactoryphpstorm

import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection.PropertyNotFoundInspectionInAttributesArrayKeysInDefinitionMethod
import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection.PropertyNotFoundInspectionInDefinitionMethod
import com.jetbrains.php.lang.inspections.PhpInspection

internal class DefinitionMethodTest : EssentialTestCase() {
    override fun getTestDataPath() = "src/test/testData/definition"
    override fun propertyNotFoundInspection(): PhpInspection = PropertyNotFoundInspectionInDefinitionMethod()
    override fun propertyNotFoundInAttributesArrayInspection(): PhpInspection =
        PropertyNotFoundInspectionInAttributesArrayKeysInDefinitionMethod()

    override fun testItCompletesPropertiesAsArrayKeysOfAttributesArrayInDirectlyPassedClosure() {
        // Not relevant
    }

    override fun testItCompletesObjectPropertiesForAttributesArrayInDirectlyPassedShortClosure() {
        // Not relevant
    }
}