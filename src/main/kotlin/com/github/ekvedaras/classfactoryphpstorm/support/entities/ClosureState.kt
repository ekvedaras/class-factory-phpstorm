package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.github.ekvedaras.classfactoryphpstorm.support.ClassFactoryPhpTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isShortClosure
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class ClosureState(val closure: Function) {

    fun resolveReturnedTypeFromClassFactory(using: ClassFactoryPhpTypeProvider): PhpType? {
        if (closure.type.filterMixed() != PhpType.EMPTY) {
            return closure.type.filterMixed()
        }

        if (closure.parameters.isEmpty()) return null

        if (closure.isShortClosure()) {
            return using.getType(
                closure
                    .childrenOfType<ArrayAccessExpression>()
                    .firstOrNull {
                        it.firstPsiChild is Variable && (it.firstPsiChild as Variable).name == closure.getParameter(0)?.name
                    } ?: return null
            )
        }

        return using.getType(
            closure
                .childrenOfType<GroupStatement>()
                .firstOrNull()
                ?.childrenOfType<PhpReturn>()
                ?.filterNot { it.childrenOfType<ArrayAccessExpression>().isEmpty() }
                ?.firstOrNull {
                    it.childrenOfType<ArrayAccessExpression>()
                        .firstOrNull()?.firstPsiChild is Variable && (it.childrenOfType<ArrayAccessExpression>()
                        .firstOrNull()?.firstPsiChild as Variable).name == closure.getParameter(0)?.name
                } ?: return null
        )
    }
}