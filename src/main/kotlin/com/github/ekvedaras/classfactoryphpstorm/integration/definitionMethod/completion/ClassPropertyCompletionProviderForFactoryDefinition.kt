package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.completion

import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.support.entities.ClassFactoryPropertyDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.entities.DefinitionMethod
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.openapi.project.DumbService
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Method

class ClassPropertyCompletionProviderForFactoryDefinition : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        if (DumbService.isDumb(parameters.position.project)) return

        val definitionMethod = try {
            when (parameters.position.parent.parent.parent) {
                /**
                 * return [
                 *      'id' => '<caret>',
                 *      ^----------------
                 * ];
                 */
                is ArrayHashElement -> ClassFactoryPropertyDefinition(
                    parameters.position.parent.parent.parent as ArrayHashElement
                ).method

                /**
                 * return ['<caret>'];
                 *        ^----------
                 */
                is ArrayCreationExpression -> DefinitionMethod(
                    parameters.position.parent.parent.parent.parent.parent.parent as? Method ?: return
                )

                else -> return
            }
        } catch (e: DomainException) {
            return
        }

        val alreadyDefinedProperties = definitionMethod.definedProperties

        result.addAllElements(
            definitionMethod
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