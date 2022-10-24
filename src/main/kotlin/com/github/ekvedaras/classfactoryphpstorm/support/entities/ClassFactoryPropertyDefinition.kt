package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Function

class ClassFactoryPropertyDefinition(val definition : ArrayHashElement) {
    val key = definition.key
    val value = definition.value

    fun isClosure() = this.value?.firstPsiChild is Function
    fun asClosureState(): ClosureState? {
        return ClosureState(this.value?.firstPsiChild as Function? ?: return null)
    }
}