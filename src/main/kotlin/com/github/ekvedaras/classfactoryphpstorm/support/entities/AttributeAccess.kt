package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.type.ClassFactoryPropertyDefinitionTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.type.ClassFactoryPropertyDefinitionTypeProvider.Companion.getClassFactoryDefinitionType
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.Parameter
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class AttributeAccess(val element: ArrayAccessExpression) {
    val function : Function
    val attributesParameter : Parameter
    val accessVariable : Variable

    init {
        accessVariable = element.firstPsiChild as? Variable ?: throw Exception("Attribute must be accessed via variable. However, first PSI child is ${element.firstPsiChild.toString()}")
        function = element.parentOfType<Function>() ?: throw Exception("Attribute access expression must be inside a function")
        attributesParameter = function.getParameter(0) ?: throw Exception("Attribute access expression parent function must have parameters")

        if (attributesParameter.name != accessVariable.name) {
            throw Exception("Attribute must be accessed via first parent function parameter \"${attributesParameter.name}\" but \"${accessVariable.name}\" is used")
        }
    }

    fun getType(): PhpType = ClassFactoryPropertyDefinitionTypeProvider().getType(this.element) ?: element.type
}