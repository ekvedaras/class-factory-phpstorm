package com.github.ekvedaras.classfactoryphpstorm.entities

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.getActualClassReference
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.getClass
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryMakeMethod
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference

class MakeMethodReference(private val methodReference: MethodReference) {
    val classFactory: ClassFactory

    init {
        if (! methodReference.isClassFactoryMakeMethod()) throw Exception("Given PSI method reference is not to ClassFactory make method.")
        classFactory = ClassFactory(methodReference.getActualClassReference()?.getClass() ?: throw Exception("Failed to load ClassFactory from make method reference"))
    }

    val definedProperties: List<ArrayHashElement>
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