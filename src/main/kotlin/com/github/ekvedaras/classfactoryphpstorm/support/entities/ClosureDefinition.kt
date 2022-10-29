package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Function

/**
 * protected function definition(): array
 * {
 *      return [
 *          'id' => fn () => 1,
 *                   ^---------
 *      ];
 * }
 */
class ClosureDefinition(closure: Function) {
    companion object {
        fun Function.asClosureDefinition(): ClosureDefinition? = try {
            ClosureDefinition(this)
        } catch (e: DomainException) {
            null
        }
    }

    val definition: ClassFactoryPropertyDefinition = ClassFactoryPropertyDefinition(
        closure.parent.parent.parent as? ArrayHashElement
            ?: throw ClosureDefinitionException.notChildOfArrayHashElement(closure.parent.parent.parent)
    )
}

internal class ClosureDefinitionException(message: String) : DomainException(message) {
    companion object {
        fun notChildOfArrayHashElement(given: PsiElement) =
            ClosureDefinitionException("Closure is not child of array hash element. Found parent: ${given.javaClass}")
    }
}