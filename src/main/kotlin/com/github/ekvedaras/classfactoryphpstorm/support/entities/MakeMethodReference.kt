package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.getActualClassReference
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.getClass
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryMakeMethod
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference

class MakeMethodReference(private val methodReference: MethodReference) : ClassFactoryMethodReference {
    override val classFactory: ClassFactory

    init {
        if (!methodReference.isClassFactoryMakeMethod()) throw MakeMethodReferenceException.notMakeMethodReference()
        classFactory = ClassFactory(
            methodReference.getActualClassReference()?.getClass()
                ?: throw MakeMethodReferenceException.unableToFindMethodClass()
        )
    }

    override val definedProperties: List<ArrayHashElement>
        get() {
            var properties = arrayOf<ArrayHashElement>()

            val stateArray = methodReference.parameterList?.getParameter(0)
            if (stateArray !is ArrayCreationExpression) return properties.toList()

            stateArray.childrenOfType<ArrayHashElement>().forEach { propertyDefinition: ArrayHashElement ->
                properties += propertyDefinition
            }

            return properties.toList()
        }
}

internal class MakeMethodReferenceException(message: String) : DomainException(message) {
    companion object {
        fun notMakeMethodReference() =
            MakeMethodReferenceException("Given PSI method reference is not to ClassFactory make method.")

        fun unableToFindMethodClass() =
            MakeMethodReferenceException("Failed to load make the class of make method reference")
    }
}