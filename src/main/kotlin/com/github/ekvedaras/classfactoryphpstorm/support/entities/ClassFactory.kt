package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.getClass
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.getFirstClass
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactory
import com.intellij.openapi.project.Project
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ClassConstantReference
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpClassFieldsList
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class ClassFactory(private val clazz: PhpClass) {
    companion object {
        fun PhpType.asClassFactory(project: Project): ClassFactory? {
            return try {
                ClassFactory(this.getFirstClass(project) ?: return null)
            } catch (e: DomainException) {
                null
            }
        }
    }


    val targetClass: TargetClass
    val definitionMethod: DefinitionMethod

    init {
        if (!clazz.isClassFactory()) throw ClassFactoryException.givenClassIsNotClassFactory()

        targetClass = TargetClass(
            this.getClassField()?.getClassReference()?.getClass()
                ?: throw ClassFactoryException.unableToFindTargetClass()
        )
        definitionMethod = DefinitionMethod(clazz.ownMethods.firstOrNull { it.name == "definition" }
            ?: throw ClassFactoryException.unableToFindDefinitionMethod(), this)
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

internal class ClassFactoryException(message: String) : DomainException(message) {
    companion object {
        fun givenClassIsNotClassFactory() = ClassFactoryException("Given PSI class must be an instance of ClassFactory")
        fun unableToFindTargetClass() = ClassFactoryException("Unable to find class factory target class")
        fun unableToFindDefinitionMethod() = ClassFactoryException("Unable to find class factory definition method")
    }
}