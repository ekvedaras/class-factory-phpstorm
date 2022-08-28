package com.github.ekvedaras.classfactoryphpstorm.outsideClassFactory.state

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryStateMethod
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.entities.StateMethodReferenceOutsideFactory
import com.intellij.codeInsight.completion.*
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference

class ClassPropertyCompletionProviderForStateMethod : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val array = parameters.position.parent.parent.parent

        if (array !is ArrayHashElement && array !is ArrayCreationExpression) return
        if (array is ArrayHashElement && parameters.position.parent.isArrayHashValueOf(array)) return
        if (array is ArrayHashElement && array.parent.parent.parent !is MethodReference) return
        if (array is ArrayCreationExpression && array.parent.parent !is MethodReference) return

        val methodReference = array.parentOfType<MethodReference>() ?: return
        if (! methodReference.isClassFactoryStateMethod()) return

        val makeMethodReference = StateMethodReferenceOutsideFactory(methodReference)
        val targetClass = makeMethodReference.classFactory.targetClass ?: return

        val alreadyDefinedProperties = makeMethodReference.definedProperties

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