package com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.completion

import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryStateMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.support.entities.ClassFactoryMethodReference
import com.github.ekvedaras.classfactoryphpstorm.support.entities.MakeMethodReference
import com.github.ekvedaras.classfactoryphpstorm.support.entities.StateMethodReferenceInsideFactory
import com.github.ekvedaras.classfactoryphpstorm.support.entities.StateMethodReferenceOutsideFactory
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.openapi.project.DumbService
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference

class ClassPropertyCompletionProviderForArrayKeysInDirectlyPassedClosure :
    CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        if (DumbService.isDumb(parameters.position.project)) return

        val key = parameters.position
        val arrayReturnedByClosure = key.parent.parent.parent.parent

        if (key.parent.parent.parent !is ArrayHashElement) return
        if (arrayReturnedByClosure !is ArrayCreationExpression) return

        val function = arrayReturnedByClosure.parentOfType<Function>() ?: return
        if (function.parent.parent.parent !is MethodReference) return
        val methodReference = function.parent.parent.parent as MethodReference

        val classFactoryMethodReference: ClassFactoryMethodReference = when (true) {
            methodReference.isClassFactoryState() -> StateMethodReferenceInsideFactory(methodReference)
            methodReference.isClassFactoryMakeMethod() -> MakeMethodReference(methodReference)
            methodReference.isClassFactoryStateMethod() -> StateMethodReferenceOutsideFactory(methodReference)
            else -> return
        }

        val targetClass = classFactoryMethodReference.classFactory.targetClass ?: return

        val alreadyDefinedProperties = classFactoryMethodReference.definedProperties

        result.addAllElements(
            targetClass
                .properties
                .filterNot {
                    alreadyDefinedProperties.find { definedProperty ->
                        it.name == definedProperty.key?.text?.unquoteAndCleanup()
                    } != null
                }
                .map { it.lookup }
        )

        result.stopHere()
    }

}