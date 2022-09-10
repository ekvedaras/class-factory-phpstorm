package com.github.ekvedaras.classfactoryphpstorm

import com.github.ekvedaras.classfactoryphpstorm.insideClassFactory.state.PropertyNotFoundInspectionInAttributesArrayKeysInState
import com.jetbrains.php.lang.inspections.PhpInspection

internal class StateMethodInsideFactoryTest : EssentialTestCase() {
    override fun getTestDataPath() = "src/test/testData/state/insideFactory"
    override fun propertyNotFoundInspection(): PhpInspection = PropertyNotFoundInspection()
    override fun propertyNotFoundInAttributesArrayInspection(): PhpInspection =
        PropertyNotFoundInspectionInAttributesArrayKeysInState()

    fun testItCompletesAttributesInStateOfAccountFactory() {
        myFixture.configureByFile("accountFactory.php")
        myFixture.completeBasic()

        assertCompletionContains("id", "exists", "accountReference", "name", "tradingName", "currency", "ratingInputs")
    }
}