package com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.type

import com.github.ekvedaras.classfactoryphpstorm.support.ClassFactoryPhpTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryStateMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isNthFunctionParameter
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.support.entities.ClassFactoryMethodReference
import com.github.ekvedaras.classfactoryphpstorm.support.entities.ClosureState
import com.github.ekvedaras.classfactoryphpstorm.support.entities.MakeMethodReference
import com.github.ekvedaras.classfactoryphpstorm.support.entities.StateMethodReferenceInsideFactory
import com.github.ekvedaras.classfactoryphpstorm.support.entities.StateMethodReferenceOutsideFactory
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.ArrayIndex
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4

class AttributesArrayValueTypeProvider : ClassFactoryPhpTypeProvider {
    companion object {
        fun PhpTypedElement.getClassFactoryStateType() = AttributesArrayValueTypeProvider().getType(this)
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
        if (! (element.firstPsiChild as Variable).isNthFunctionParameter(function)) return null

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

        val classFactoryMethodReference: ClassFactoryMethodReference = when (true) {
            methodReference.isClassFactoryState() -> StateMethodReferenceInsideFactory(methodReference)
            methodReference.isClassFactoryMakeMethod() -> MakeMethodReference(methodReference)
            methodReference.isClassFactoryStateMethod() -> StateMethodReferenceOutsideFactory(methodReference)
            else -> return null
        }

        val definitionMethod = classFactoryMethodReference.classFactory.definitionMethod ?: return null
        val propertyDefinition =
            definitionMethod.getPropertyDefinition(key.text.unquoteAndCleanup()) ?: return null

        if (propertyDefinition.isClosure()) {
            return propertyDefinition.asClosureState()?.resolveReturnedTypeFromClassFactory(this)
        }

        if (propertyDefinition.value !is PhpTypedElement) return null

        return propertyDefinition.value.type
    }

    override fun complete(p0: String?, p1: Project?): PhpType? {
        return null
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