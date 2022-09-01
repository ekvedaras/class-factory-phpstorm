package com.github.ekvedaras.classfactoryphpstorm.insideClassFactory.definition

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.entities.DefinitionMethod
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.Function

class ClassPropertyCompletionProviderForAttributesArrayInClosureOfFactoryDefinition : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val key = parameters.position.parent
        val attributesArray = key.parent.parent

        if (key.parent !is ArrayIndex) return
        if (attributesArray !is ArrayAccessExpression) return
        if (attributesArray.firstPsiChild !is Variable) return

        val function = attributesArray.parentOfType<Function>() ?: return
        if (function.parent.parent.parent !is ArrayHashElement) return
        if (function.parameters[0].name != (attributesArray.firstPsiChild as Variable).name) return

        val arrayHashElement = function.parent.parent.parent

        if (arrayHashElement !is ArrayHashElement) return
        if (arrayHashElement.parent.parent !is PhpReturn) return

        val method = arrayHashElement.parentOfType<Method>() ?: return
        if (! method.isClassFactoryDefinition()) return

        val definitionMethod = DefinitionMethod(method)
        val targetClass = definitionMethod.classFactory.targetClass ?: return

        result.addAllElements(
            targetClass
                .constructor
                ?.parameters
                ?.map { it.lookup } ?: return
        )

        result.stopHere()
    }
}