package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.completion

import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.support.entities.DefinitionMethod
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.openapi.project.DumbService
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpReturn

class ClassPropertyCompletionProviderForFactoryDefinition : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        if (DumbService.isDumb(parameters.position.project)) return

        val array = parameters.position.parent.parent.parent

        if (array !is ArrayHashElement && array !is ArrayCreationExpression) return
        if (array is ArrayHashElement && parameters.position.parent.isArrayHashValueOf(array)) return
        if (array is ArrayHashElement && array.parent.parent !is PhpReturn) return
        if (array is ArrayCreationExpression && array.parent !is PhpReturn) return

        val method = array.parentOfType<Method>() ?: return
        if (!method.isClassFactoryDefinition()) return

        val definitionMethod = DefinitionMethod(method)
        val targetClass = definitionMethod.classFactory.targetClass ?: return

        val alreadyDefinedProperties = definitionMethod.definedProperties

        result.addAllElements(
            targetClass
                .properties
                .filterNot {
                    alreadyDefinedProperties.find { definedProperty ->
                        it.name == definedProperty.key?.text?.unquoteAndCleanup()
                    } != null
                }
                .map { it.lookup } ?: return
        )

        result.stopHere()
    }
}