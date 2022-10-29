package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.getActualClassReference
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.getClass
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryStateMethod
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference

class StateMethodReferenceOutsideFactory(private val methodReference: MethodReference) : ClassFactoryMethodReference {
    override val classFactory: ClassFactory

    init {
        if (!methodReference.isClassFactoryStateMethod()) throw StateMethodReferenceOutsideFactoryException.notStateMethodReference()
        classFactory = ClassFactory(
            methodReference.getActualClassReference()?.getClass()
                ?: throw StateMethodReferenceOutsideFactoryException.unableToFindMethodClass()
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

internal class StateMethodReferenceOutsideFactoryException(message: String) : DomainException(message) {
    companion object {
        fun notStateMethodReference() =
            StateMethodReferenceOutsideFactoryException("Given PSI method reference is not to ClassFactory state method outside factory.")

        fun unableToFindMethodClass() =
            StateMethodReferenceOutsideFactoryException("Failed to load make the class of state method reference")
    }
}