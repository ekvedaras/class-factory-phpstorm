package com.github.ekvedaras.classfactoryphpstorm.classfactory.state

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.entities.StateMethodReference
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference

class ClassPropertyCompletionProviderForFactoryState : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val element = parameters.position.parent.parent.parent

        if (element !is ArrayHashElement && element !is ArrayCreationExpression) return

        val methodReference = element.parentOfType<MethodReference>() ?: return
        if (! methodReference.isClassFactoryState()) return

        val stateMethodReference = StateMethodReference(methodReference)
        val targetClass = stateMethodReference.classFactory.targetClass ?: return

        val alreadyDefinedProperties = stateMethodReference.definedProperties

        result.addAllElements(
            targetClass
                .constructor
                ?.parameters
                ?.filterNot { alreadyDefinedProperties.find { definedProperty ->
                    it.parameter.name == definedProperty.key?.text?.unquoteAndCleanup()
                } != null }
                ?.map { it.lookup } ?: return
        )

        result.stopHere()
    }
}