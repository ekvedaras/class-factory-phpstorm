package com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.type

import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.type.ClassFactoryPropertyDefinitionTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.support.ClassFactoryPhpTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.getClass
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isMostLikelyClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isMostLikelyClassFactoryStateMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isNthFunctionParameter
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.domain.ClassFactory
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.ArrayIndex
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class AttributesArrayValueTypeProvider : ClassFactoryPhpTypeProvider {
    companion object {
        fun PhpTypedElement.getClassFactoryStateType(): PhpType? {
            val provider = AttributesArrayValueTypeProvider()
            val type = provider.getType(this)

            if (type?.isComplete == true) return type

            return provider.complete(type.toString(), this.project)
        }
    }

    override fun getKey(): Char {
        return '\u0311'
    }

    override fun getType(element: PsiElement): PhpType? {
        if (DumbService.isDumb(element.project)) return null

        if (element !is ArrayAccessExpression) return null
        if (element.firstPsiChild !is Variable) return null

        val key = element.childrenOfType<ArrayIndex>().firstOrNull()?.firstPsiChild ?: return null
        if (key !is StringLiteralExpression) return null

        val function = element.parentOfType<Function>() ?: return null
        if (!(element.firstPsiChild as Variable).isNthFunctionParameter(function)) return null

        if (function.parent.parent.parent !is ArrayHashElement && function.parent.parent.parent !is MethodReference) return null

        val methodReference = if (function.parent.parent.parent is ArrayHashElement) {
            val arrayHashElement = function.parent.parent.parent

            if (arrayHashElement !is ArrayHashElement) return null
            if (!function.parent.isArrayHashValueOf(arrayHashElement)) return null
            if (arrayHashElement.parent.parent.parent !is MethodReference) return null

            arrayHashElement.parentOfType() ?: return null
        } else {
            function.parent.parent.parent as MethodReference
        }

        if (!methodReference.isClassFactoryState() && !methodReference.isMostLikelyClassFactoryMakeMethod() && !methodReference.isMostLikelyClassFactoryStateMethod()) {
            return null
        }

        if (methodReference.isClassFactoryState()) {
            return PhpType().add("#${this.key}${methodReference.parentOfType<PhpClass>()?.fqn}.${key.text.unquoteAndCleanup()}")
        }


        return PhpType().add("#${this.key}${((methodReference.classReference as MethodReference).classReference as ClassReference).fqn}.${key.text.unquoteAndCleanup()}")
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

        val propertyDefinition = classFactory
            .definitionMethod
            .getPropertyDefinition(key) ?: return null

        if (propertyDefinition.isClosure()) {
            return propertyDefinition.asClosureState()
                ?.resolveReturnedTypeFromClassFactory(ClassFactoryPropertyDefinitionTypeProvider())
        }

        return propertyDefinition.value.type
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