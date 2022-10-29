package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.github.ekvedaras.classfactoryphpstorm.support.ClassFactoryPhpTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isShort
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class ClosureState(val closure: Function) {

    fun resolveReturnedTypeFromClassFactory(using: ClassFactoryPhpTypeProvider): PhpType? {
        if (closure.type.isComplete && closure.type.filterMixed() != PhpType.EMPTY) {
            return closure.type.filterMixed()
        }

        if (closure.parameters.isEmpty()) return null

        if (closure.isShort()) { // TODO: returns false for: fn (array $attributes) => $attributes['firstName']
            val type = using.getType(
                closure
                    .childrenOfType<ArrayAccessExpression>()
                    .firstOrNull {
                        it.firstPsiChild is Variable && (it.firstPsiChild as Variable).name == closure.getParameter(0)?.name
                    } ?: return null
            )

            if (type?.isComplete == true) return type

            return using.complete(type.toString(), closure.project)
        }

        val type = using.getType(
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

        return using.complete(type.toString(), closure.project)
    }
}