package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.type.ClassFactoryPropertyDefinitionTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.Parameter
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.resolve.types.PhpType

/**
 * function (array $attributes) {
 *      return $attributes['id'];
 *             ^----------------
 * }
 */
class AttributeAccess(val element: ArrayAccessExpression) {
    val function: Function
    val attributesParameter: Parameter
    val accessVariable: Variable

    init {
        accessVariable =
            element.firstPsiChild as? Variable ?: throw AttributeAccessException.variableNotFound(element.firstPsiChild)
        function = element.parentOfType() ?: throw AttributeAccessException.notInsideAFunction()
        attributesParameter = function.getParameter(0) ?: throw AttributeAccessException.parentFunctionHasNoParameters()

        if (attributesParameter.name != accessVariable.name) {
            throw AttributeAccessException.notAttributesVariable(attributesParameter, accessVariable)
        }
    }

    fun getCompleteType(): PhpType {
        val typeProvider = ClassFactoryPropertyDefinitionTypeProvider()
        val type = typeProvider.getType(this.element) ?: element.type

        if (type.isComplete) return type

        return typeProvider.complete(type.toString(), this.element.project) ?: element.type
    }
}

internal class AttributeAccessException(message: String) : DomainException(message) {
    companion object {
        fun variableNotFound(found: PsiElement?) =
            AttributeAccessException("Attribute must be accessed via variable. However, first PSI child is ${found.toString()}")

        fun notInsideAFunction() = AttributeAccessException("Attribute access expression must be inside a function")
        fun parentFunctionHasNoParameters() =
            AttributeAccessException("Attribute access expression parent function must have parameters")

        fun notAttributesVariable(attributesParameter: Parameter, variable: Variable) =
            AttributeAccessException("Attribute must be accessed via first parent function parameter \"${attributesParameter.name}\" but \"${variable.name}\" is used")
    }
}