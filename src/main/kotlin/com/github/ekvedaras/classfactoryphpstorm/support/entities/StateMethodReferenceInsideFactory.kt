package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryState
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference

class StateMethodReferenceInsideFactory(private val methodReference: MethodReference) : ClassFactoryMethodReference {
    override val classFactory: ClassFactory

    init {
        if (!methodReference.isClassFactoryState()) throw Exception("Given PSI method reference is not to ClassFactory state method inside factory.")
        classFactory = ClassFactory(
            methodReference.parentOfType<Method>()?.containingClass
                ?: throw Exception("Failed to load ClassFactory from state method reference")
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