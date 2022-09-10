package com.github.ekvedaras.classfactoryphpstorm

import com.github.ekvedaras.classfactoryphpstorm.outsideClassFactory.state.PropertyNotFoundInspectionInStateMethod
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.jetbrains.php.lang.inspections.PhpInspection

internal class StateMethodOutsideFactoryTest : EssentialTestCase() {
    override fun getTestDataPath() = "src/test/testData/state/outsideFactory"
    override fun propertyNotFoundInspection(): PhpInspection = PropertyNotFoundInspectionInStateMethod()
}