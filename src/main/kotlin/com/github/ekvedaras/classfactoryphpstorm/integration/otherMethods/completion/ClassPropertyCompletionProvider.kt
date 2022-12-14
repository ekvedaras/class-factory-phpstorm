package com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.completion

import com.github.ekvedaras.classfactoryphpstorm.domain.ClassFactory.Companion.asClassFactory
import com.github.ekvedaras.classfactoryphpstorm.domain.ClassFactoryMethodReference
import com.github.ekvedaras.classfactoryphpstorm.domain.closureState.AttributeAccess
import com.github.ekvedaras.classfactoryphpstorm.domain.method.make.MakeMethodReference
import com.github.ekvedaras.classfactoryphpstorm.domain.method.state.StateMethodReferenceInsideFactory
import com.github.ekvedaras.classfactoryphpstorm.domain.method.state.StateMethodReferenceOutsideFactory
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.type.AttributesArrayValueTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactory
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryStateMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiReference
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference

class ClassPropertyCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        if (DumbService.isDumb(parameters.position.project)) return

        val array = parameters.position.parent.parent.parent

        if (array !is ArrayHashElement && array !is ArrayCreationExpression) return
        if (array is ArrayHashElement && parameters.position.parent.isArrayHashValueOf(array)) return
        if (array is ArrayHashElement && array.parent.parent.parent !is MethodReference) return
        if (array is ArrayCreationExpression && array.parent.parent !is MethodReference) return

        val methodReference = array.parentOfType<MethodReference>() ?: return

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

        val alreadyDefinedProperties = classFactoryMethodReference.definedProperties

        result.addAllElements(
            classFactoryMethodReference
                .classFactory
                .targetClass
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