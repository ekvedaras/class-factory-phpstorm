package com.github.ekvedaras.classfactoryphpstorm.classfactoryusages.make

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.entities.MakeMethodReference
import com.intellij.codeInsight.completion.*
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference

class ClassPropertyCompletionProviderForMakeMethod : CompletionProvider<CompletionParameters>() {
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
        if (! methodReference.isClassFactoryMakeMethod()) return

        val makeMethodReference = MakeMethodReference(methodReference)
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