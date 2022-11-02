package com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.completion

import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryStateMethod
import com.github.ekvedaras.classfactoryphpstorm.domain.closureState.AttributeAccess
import com.github.ekvedaras.classfactoryphpstorm.domain.ClassFactoryMethodReference
import com.github.ekvedaras.classfactoryphpstorm.domain.method.make.MakeMethodReference
import com.github.ekvedaras.classfactoryphpstorm.domain.method.state.StateMethodReferenceInsideFactory
import com.github.ekvedaras.classfactoryphpstorm.domain.method.state.StateMethodReferenceOutsideFactory
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.openapi.project.DumbService
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference

class ClassPropertyCompletionProviderForAttributesArrayInClosure :
    CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        if (DumbService.isDumb(parameters.position.project)) return

        val attributeAccess = try {
            AttributeAccess(parameters.position.parent.parent.parent as? ArrayAccessExpression ?: return)
        } catch (e: DomainException) { return }

        val function = attributeAccess.function

        if (function.parent.parent.parent !is ArrayHashElement && function.parent.parent.parent !is MethodReference) return

        val methodReference = if (function.parent.parent.parent is ArrayHashElement) {
            val array = function.parent.parent.parent

            if (array !is ArrayHashElement) return
            if (array.parent.parent.parent !is MethodReference) return

            array.parentOfType() ?: return
        } else {
            function.parent.parent.parent as MethodReference
        }

        val classFactoryMethodReference: ClassFactoryMethodReference = try {
            when (true) {
                methodReference.isClassFactoryState() -> StateMethodReferenceInsideFactory(methodReference)
                methodReference.isClassFactoryMakeMethod() -> MakeMethodReference(methodReference)
                methodReference.isClassFactoryStateMethod() -> StateMethodReferenceOutsideFactory(methodReference)
                else -> return
            }
        } catch (e: DomainException) {
            return
        }

        result.addAllElements(
            classFactoryMethodReference
                .classFactory
                .targetClass
                .properties
                .map { it.lookup }
        )

        result.stopHere()
    }

}