package com.github.ekvedaras.classfactoryphpstorm.domain.method.definition

import com.github.ekvedaras.classfactoryphpstorm.domain.closureState.ClosureState
import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.type.ClassFactoryPropertyDefinitionTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.resolve.types.PhpType

/**
 * protected function definition(): array
 * {
 *      return [
 *          'id' => 1,
 *          ^--------
 *      ];
 * }
 */
class ClassFactoryPropertyDefinition(val element: ArrayHashElement) {
    val key: StringLiteralExpression
    val value: PhpTypedElement
    val propertyName: String
    val method: DefinitionMethod

    init {
        if (element.parent.parent !is PhpReturn) throw ClassFactoryPropertyDefinitionException.notDirectlyReturned()

        key = element.key as? StringLiteralExpression ?: throw ClassFactoryPropertyDefinitionException.noStringKey()
        value = element.value as? PhpTypedElement ?: throw ClassFactoryPropertyDefinitionException.noTypedValue(key)
        propertyName = key.text.unquoteAndCleanup()
        method =
            DefinitionMethod(element.parentOfType() ?: throw ClassFactoryPropertyDefinitionException.notInsideAMethod())
    }

    fun isClosure() = this.value.firstPsiChild is Function
    fun asClosureState(): ClosureState? {
        return ClosureState(this.value.firstPsiChild as Function? ?: return null)
    }

    fun typeForDefinition(): PhpType {
        val provider = ClassFactoryPropertyDefinitionTypeProvider()

        if (this.isClosure()) {
            return this.asClosureState()?.resolveReturnedTypeFromClassFactory(provider) ?: this.value.type
        }

        val type = provider.getType(this.value)

        if (type?.isComplete == true) return type

        return provider.complete(type.toString(), element.project) ?: this.value.type
    }
}

internal class ClassFactoryPropertyDefinitionException(message: String) : DomainException(message) {
    companion object {
        fun notDirectlyReturned() =
            ClassFactoryPropertyDefinitionException("Definition must be element of directly returned array")

        fun noStringKey() = ClassFactoryPropertyDefinitionException("Class property definition has no string key")
        fun noTypedValue(forKey: StringLiteralExpression) =
            ClassFactoryPropertyDefinitionException("Class property definition for key $forKey has no typed value")

        fun notInsideAMethod() = ClassFactoryPropertyDefinitionException("Definition is not inside a method")
    }
}