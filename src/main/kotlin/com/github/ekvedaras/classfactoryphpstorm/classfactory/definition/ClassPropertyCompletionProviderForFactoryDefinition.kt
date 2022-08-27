package com.github.ekvedaras.classfactoryphpstorm.classfactory.definition

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.entities.DefinitionMethod
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Method

class ClassPropertyCompletionProviderForFactoryDefinition : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val array = parameters.position.parent.parent.parent

        if (array !is ArrayHashElement && array !is ArrayCreationExpression) return
        if (array is ArrayHashElement && parameters.position.parent.isArrayHashValueOf(array)) return

        val method = array.parentOfType<Method>() ?: return
        if (! method.isClassFactoryDefinition()) return

        val definitionMethod = DefinitionMethod(method)
        val targetClass = definitionMethod.classFactory.targetClass ?: return

        val alreadyDefinedProperties = definitionMethod.definedProperties

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