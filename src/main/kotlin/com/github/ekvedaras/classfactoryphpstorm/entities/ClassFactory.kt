package com.github.ekvedaras.classfactoryphpstorm.entities

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactory
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.*

class ClassFactory(private val clazz: PhpClass) {
    init {
        if (! clazz.isClassFactory()) throw Exception("Given PSI class must be an instance of ClassFactory")
    }

    val targetClass: TargetClass?
        get() {
            return TargetClass(
                PhpIndex
                .getInstance(clazz.project)
                .getClassesByFQN(this.getClassField()?.getClassReference()?.fqn ?: return null)
                .firstOrNull() ?: return null
            )
        }

    private fun getClassField() = clazz.childrenOfType<PhpClassFieldsList>()
        .firstOrNull { fieldList -> fieldList.childrenOfType<Field>().firstOrNull{ it.name == "class" } != null }
        ?.childrenOfType<Field>()
        ?.firstOrNull { it.name == "class" }

    private fun Field.getClassReference() = this
        .childrenOfType<ClassConstantReference>()
        .firstOrNull()
        ?.childrenOfType<ClassReference>()
        ?.firstOrNull()
}