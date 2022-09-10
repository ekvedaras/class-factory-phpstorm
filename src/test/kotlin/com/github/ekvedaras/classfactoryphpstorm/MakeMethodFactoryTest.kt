package com.github.ekvedaras.classfactoryphpstorm

import com.github.ekvedaras.classfactoryphpstorm.outsideClassFactory.make.PropertyNotFoundInspectionInMakeMethod
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.jetbrains.php.lang.inspections.PhpInspection

internal class MakeMethodFactoryTest : EssentialTestCase() {
    override fun getTestDataPath() = "src/test/testData/make"
    override fun propertyNotFoundInspection(): PhpInspection = PropertyNotFoundInspectionInMakeMethod()
}