package com.github.ekvedaras.classfactoryphpstorm.entities

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.getClass
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactory
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ClassConstantReference
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpClassFieldsList

class ClassFactory(private val clazz: PhpClass) {
    init {
        if (!clazz.isClassFactory()) throw Exception("Given PSI class must be an instance of ClassFactory")
    }

    val targetClass: TargetClass?
        get() {
            return TargetClass(this.getClassField()?.getClassReference()?.getClass() ?: return null)
        }

    val definitionMethod: DefinitionMethod?
        get() {
            return DefinitionMethod(clazz.ownMethods.firstOrNull { it.name == "definition" } ?: return null)
        }

    private fun getClassField() = clazz.childrenOfType<PhpClassFieldsList>()
        .firstOrNull { fieldList -> fieldList.childrenOfType<Field>().firstOrNull { it.name == "class" } != null }
        ?.childrenOfType<Field>()
        ?.firstOrNull { it.name == "class" }

    private fun Field.getClassReference() = this
        .childrenOfType<ClassConstantReference>()
        .firstOrNull()
        ?.childrenOfType<ClassReference>()
        ?.firstOrNull()
}