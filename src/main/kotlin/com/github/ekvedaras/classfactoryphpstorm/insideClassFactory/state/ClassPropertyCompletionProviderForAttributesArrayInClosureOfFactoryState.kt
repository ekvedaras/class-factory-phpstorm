package com.github.ekvedaras.classfactoryphpstorm.insideClassFactory.state

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isCurrentClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.entities.StateMethodReferenceInsideFactory
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.Function

class ClassPropertyCompletionProviderForAttributesArrayInClosureOfFactoryState : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val key = parameters.position
        val attributesArray = key.parent.parent.parent

        if (key.parent.parent !is ArrayIndex) return
        if (attributesArray !is ArrayAccessExpression) return
        if (attributesArray.firstPsiChild !is Variable) return

        val function = attributesArray.parentOfType<Function>() ?: return
        if (function.parent.parent.parent !is ArrayHashElement) return
        if (function.parameters[0].name != (attributesArray.firstPsiChild as Variable).name) return

        val array = function.parent.parent.parent

        if (array !is ArrayHashElement) return
        if (array.parent.parent.parent !is MethodReference) return

        val methodReference = array.parentOfType<MethodReference>() ?: return
        if (! methodReference.isCurrentClassFactoryState()) return

        val stateMethodReference = StateMethodReferenceInsideFactory(methodReference)
        val targetClass = stateMethodReference.classFactory.targetClass ?: return

        result.addAllElements(
            targetClass
                .constructor
                ?.parameters
                ?.map { it.lookup } ?: return
        )

        result.stopHere()
    }
}