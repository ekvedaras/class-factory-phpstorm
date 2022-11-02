package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.completion

import com.github.ekvedaras.classfactoryphpstorm.domain.closureState.AttributeAccess
import com.github.ekvedaras.classfactoryphpstorm.domain.method.definition.ClosureDefinition.Companion.asClosureDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.openapi.project.DumbService
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression

/**
 * protected function definition(): array
 * {
 *      return [
 *          'id' => function (array $attributes) {
 *              return $attributes['<caret>'];
 *                                   ^------
 *          },
 *      ];
 * }
 */
class ClassPropertyCompletionProviderForAttributesArrayInClosureOfFactoryDefinition :
    CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        if (DumbService.isDumb(parameters.position.project)) return

        val attributeAccess = try {
            AttributeAccess(parameters.position.parent.parent.parent as? ArrayAccessExpression ?: return)
        } catch (e: DomainException) {
            return
        }

        val closureDefinition = attributeAccess.function.asClosureDefinition() ?: return

        result.addAllElements(
            closureDefinition
                .definition
                .method
                .classFactory
                .targetClass
                .properties
                .map { it.lookup }
        )

        result.stopHere()
    }
}