package com.github.ekvedaras.classfactoryphpstorm

import com.github.ekvedaras.classfactoryphpstorm.insideClassFactory.state.PropertyNotFoundInspectionInState
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.jetbrains.php.lang.inspections.PhpInspection

internal class StateMethodInsideFactoryTest : EssentialTestCase() {
    override fun getTestDataPath() = "src/test/testData/state/insideFactory"
    override fun propertyNotFoundInspection(): PhpInspection = PropertyNotFoundInspectionInState()

    fun testItCompletesAttributesInStateOfAccountFactory() {
        myFixture.configureByFile("accountFactory.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "exists", "accountReference", "name", "tradingName", "currency", "ratingInputs")
    }
}