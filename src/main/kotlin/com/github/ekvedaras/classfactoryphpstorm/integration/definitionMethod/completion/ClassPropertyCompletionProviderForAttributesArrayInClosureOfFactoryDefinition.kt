package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.completion

import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.entities.DefinitionMethod
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.openapi.project.DumbService
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.ArrayIndex
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.Variable

class ClassPropertyCompletionProviderForAttributesArrayInClosureOfFactoryDefinition :
    CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        if (DumbService.isDumb(parameters.position.project)) return

        val key = parameters.position.parent
        val attributesArray = key.parent.parent

        if (key.parent !is ArrayIndex) return
        if (attributesArray !is ArrayAccessExpression) return
        if (attributesArray.firstPsiChild !is Variable) return

        val function = attributesArray.parentOfType<Function>() ?: return
        if (function.parent.parent.parent !is ArrayHashElement) return
        if (function.parameters.isEmpty() || function.parameters[0].name != (attributesArray.firstPsiChild as Variable).name) return

        val arrayHashElement = function.parent.parent.parent

        if (arrayHashElement !is ArrayHashElement) return
        if (arrayHashElement.parent.parent !is PhpReturn) return

        val method = arrayHashElement.parentOfType<Method>() ?: return
        if (!method.isClassFactoryDefinition()) return

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