package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.type.ClassFactoryPropertyDefinitionTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isShort
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.returnedValue
import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.resolve.types.PhpType

/**
 * protected function definition(): array
 * {
 *      return [
 *          'id' => fn () => 1,
 *                   ^---------
 *      ];
 * }
 */
class ClosureDefinition(val closure: Function) {
    companion object {
        fun Function.asClosureDefinition(): ClosureDefinition? = try {
            ClosureDefinition(this)
        } catch (e: DomainException) {
            null
        }
    }

    val returnedValue = closure.returnedValue()
    val definition: ClassFactoryPropertyDefinition = ClassFactoryPropertyDefinition(
        closure.parent.parent.parent as? ArrayHashElement
            ?: throw ClosureDefinitionException.notChildOfArrayHashElement(closure.parent.parent.parent)
    )

    fun type(): PhpType? {
        if (closure.type.isComplete && closure.type.filterMixed() != PhpType.EMPTY) {
            return closure.type.filterMixed()
        }

        if (closure.parameters.isEmpty()) return null

        val typeProvider = ClassFactoryPropertyDefinitionTypeProvider()

        if (closure.isShort()) {
            val type = typeProvider.getType(
                closure
                    .childrenOfType<ArrayAccessExpression>()
                    .firstOrNull {
                        it.firstPsiChild is Variable && (it.firstPsiChild as Variable).name == closure.getParameter(0)?.name
                    } ?: return null
            )

            if (type?.isComplete == true) return type

            return typeProvider.complete(type.toString(), closure.project)
        }

        val type = typeProvider.getType(
            closure
                .childrenOfType<GroupStatement>()
                .firstOrNull()
                ?.childrenOfType<PhpReturn>()
                ?.filterNot { it.childrenOfType<ArrayAccessExpression>().isEmpty() }
                ?.firstOrNull {
                    it.childrenOfType<ArrayAccessExpression>()
                        .firstOrNull()?.firstPsiChild is Variable && (it.childrenOfType<ArrayAccessExpression>()
                        .firstOrNull()?.firstPsiChild as Variable).name == closure.getParameter(0)?.name
                }
                ?.firstPsiChild ?: return null
        )

        if (type?.isComplete == true) return type

        return typeProvider.complete(type.toString(), closure.project)
    }
}

internal class ClosureDefinitionException(message: String) : DomainException(message) {
    companion object {
        fun notChildOfArrayHashElement(given: PsiElement) =
            ClosureDefinitionException("Closure is not child of array hash element. Found parent: ${given.javaClass}")
    }
}