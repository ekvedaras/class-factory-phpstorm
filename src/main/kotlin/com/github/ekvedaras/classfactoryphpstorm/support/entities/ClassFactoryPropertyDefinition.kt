package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.type.ClassFactoryPropertyDefinitionTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.type.AttributesArrayValueTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class ClassFactoryPropertyDefinition(val element : ArrayHashElement) {
    val key : StringLiteralExpression
    val value : PhpTypedElement
    val propertyName : String
    val method : DefinitionMethod

    init {
        if (element.parent.parent !is PhpReturn) throw ClassFactoryPropertyDefinitionException.notDirectlyReturned()

        key = element.key as? StringLiteralExpression ?: throw ClassFactoryPropertyDefinitionException.noStringKey()
        value = element.value as? PhpTypedElement ?: throw ClassFactoryPropertyDefinitionException.noTypedValue(key)
        propertyName = key.text.unquoteAndCleanup()
        method = DefinitionMethod(element.parentOfType<Method>() ?: throw ClassFactoryPropertyDefinitionException.notInsideAMethod())
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

internal class ClassFactoryPropertyDefinitionException(message: String) : DomainException(message) {
    companion object {
        fun notDirectlyReturned() = ClassFactoryPropertyDefinitionException("Definition must be element of directly returned array")
        fun noStringKey() = ClassFactoryPropertyDefinitionException("Class property definition has no string key")
        fun noTypedValue(forKey: StringLiteralExpression) = ClassFactoryPropertyDefinitionException("Class property definition for key $forKey has no typed value")
        fun notInsideAMethod() = ClassFactoryPropertyDefinitionException("Definition is not inside a method")
    }
}