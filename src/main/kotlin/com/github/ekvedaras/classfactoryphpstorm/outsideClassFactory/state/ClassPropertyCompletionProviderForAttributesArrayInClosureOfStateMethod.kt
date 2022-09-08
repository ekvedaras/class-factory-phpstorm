package com.github.ekvedaras.classfactoryphpstorm.outsideClassFactory.state

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryStateMethod
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.entities.StateMethodReferenceOutsideFactory
import com.intellij.codeInsight.completion.*
import com.intellij.openapi.project.DumbService
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.Function

class ClassPropertyCompletionProviderForAttributesArrayInClosureOfStateMethod : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        if (DumbService.isDumb(parameters.position.project)) return

        val key = parameters.position
        val attributesArray = key.parent.parent.parent

        if (key.parent.parent !is ArrayIndex) return
        if (attributesArray !is ArrayAccessExpression) return
        if (attributesArray.firstPsiChild !is Variable) return

        val function = attributesArray.parentOfType<Function>() ?: return
        if (function.parameters[0].name != (attributesArray.firstPsiChild as Variable).name) return

        val methodReference = if (function.parent.parent is ArrayHashElement) {
            val array = function.parent.parent.parent

            if (array !is ArrayHashElement) return
            if (array.parent.parent.parent !is MethodReference) return

            array.parentOfType<MethodReference>() ?: return
        } else {
            function.parent.parent.parent as MethodReference
        }

        if (! methodReference.isClassFactoryStateMethod()) return

        val makeMethodReference = StateMethodReferenceOutsideFactory(methodReference)
        val targetClass = makeMethodReference.classFactory.targetClass ?: return

        val alreadyDefinedProperties = makeMethodReference.definedProperties

        result.addAllElements(
            targetClass
                .constructor
                ?.parameters
                ?.map { it.lookup } ?: return
        )

        result.stopHere()
    }

}