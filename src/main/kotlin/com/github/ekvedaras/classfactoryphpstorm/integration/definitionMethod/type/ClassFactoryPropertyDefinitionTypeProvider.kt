package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.type

import com.github.ekvedaras.classfactoryphpstorm.domain.ClassFactory
import com.github.ekvedaras.classfactoryphpstorm.domain.closureState.AttributeAccess
import com.github.ekvedaras.classfactoryphpstorm.support.ClassFactoryPhpTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.getClass
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unwrapClosureValue
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class ClassFactoryPropertyDefinitionTypeProvider : ClassFactoryPhpTypeProvider {
    companion object {
        fun PhpTypedElement.getClassFactoryDefinitionType(): PhpType? =
            ClassFactoryPropertyDefinitionTypeProvider().getType(this)
    }

    override fun getKey(): Char {
        return '\u0310'
    }

    override fun getType(element: PsiElement): PhpType? {
        if (DumbService.isDumb(element.project)) return null

        val attributeAccess = try {
            AttributeAccess(element as? ArrayAccessExpression ?: return null)
        } catch (e: DomainException) {
            return null
        }

        val function = attributeAccess.function
        if (function.parent.parent.parent !is ArrayHashElement) return null

        val arrayHashElement = function.parent.parent.parent

        if (arrayHashElement !is ArrayHashElement) return null
        if (!function.parent.isArrayHashValueOf(arrayHashElement)) return null

        if (arrayHashElement.parent.parent !is PhpReturn) return null

        val method = arrayHashElement.parentOfType<Method>() ?: return null
        if (!method.isClassFactoryDefinition()) return null

        return PhpType().add("#${this.key}${(method.containingClass as PhpClass).fqn}.${attributeAccess.attributeName}")
    }

    override fun complete(incompleteType: String?, project: Project?): PhpType? {
        if (incompleteType == null || project == null) return null

        val classFactoryReference = incompleteType.substringAfter("#${this.key}").substringBefore('.')
        val key = incompleteType.substringAfter('.').substringBefore('|')

        val classFactory = try {
            ClassFactory(classFactoryReference.getClass(project) ?: return null)
        } catch (e: DomainException) {
            return null
        }

        val propertyDefinition =
            classFactory.definitionMethod.getPropertyDefinition(key) ?: return null

        if (propertyDefinition.isClosure()) {
            return propertyDefinition.asClosureState()?.resolveReturnedTypeFromClassFactory(this)?.unwrapClosureValue(project)
        }

        return propertyDefinition.value.type.unwrapClosureValue(project)
    }

    override fun getBySignature(
        p0: String?,
        p1: Set<String>?,
        p2: Int,
        p3: Project?
    ): Collection<PhpNamedElement?> {
        return emptyList()
    }
}