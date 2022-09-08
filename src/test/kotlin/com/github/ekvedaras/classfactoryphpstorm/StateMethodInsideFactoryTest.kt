package com.github.ekvedaras.classfactoryphpstorm

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.PrioritizedLookupElement

internal class StateMethodInsideFactoryTest : EssentialTestCase() {
    override fun getTestDataPath() = "src/test/testData/state/insideFactory"

    fun testItCompletesAttributesInStateOfAccountFactory() {
        myFixture.configureByFile("accountFactory.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "exists", "accountReference", "name", "tradingName", "currency", "ratingInputs")
    }
}