package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.type.ClassFactoryPropertyDefinitionTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.type.AttributesArrayValueTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class ClassFactoryPropertyDefinition(val element : ArrayHashElement) {
    val key : StringLiteralExpression
    val value : PhpTypedElement
    val propertyName : String

    init {
        key = element.key as? StringLiteralExpression ?: throw Exception("Class property definition has no string key")
        value = element.value as? PhpTypedElement ?: throw Exception("Class property definition for key $key has no typed value")
        propertyName = key.text.unquoteAndCleanup()
    }

    fun isClosure() = this.value.firstPsiChild is Function
    fun asClosureState(): ClosureState? {
        return ClosureState(this.value.firstPsiChild as Function? ?: return null)
    }

    fun typeForDefinition(): PhpType {
        return ClassFactoryPropertyDefinitionTypeProvider().getType(this.value) ?: this.value.type
    }

    fun typeForState(): PhpType {
        return AttributesArrayValueTypeProvider().getType(this.value) ?: this.value.type
    }
}