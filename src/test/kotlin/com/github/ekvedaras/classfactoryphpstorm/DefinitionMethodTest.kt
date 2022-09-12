package com.github.ekvedaras.classfactoryphpstorm

import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection.PropertyNotFoundInspectionInAttributesArrayKeysInDefinitionMethod
import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection.PropertyNotFoundInspectionInDefinitionMethod
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection.PropertyNotFoundInspectionInArrayKeysInDirectlyPassedClosure
import com.jetbrains.php.lang.inspections.PhpInspection

internal class DefinitionMethodTest : EssentialTestCase() {
    override fun getTestDataPath() = "src/test/testData/definition"
    override fun propertyNotFoundInspection(): PhpInspection = PropertyNotFoundInspectionInDefinitionMethod()
    override fun propertyNotFoundInAttributesArrayInspection(): PhpInspection =
        PropertyNotFoundInspectionInAttributesArrayKeysInDefinitionMethod()

    override fun propertyNotFoundInArrayKeysInDirectlyPassedClosure(): PhpInspection =
        PropertyNotFoundInspectionInArrayKeysInDirectlyPassedClosure()

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
}