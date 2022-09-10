package com.github.ekvedaras.classfactoryphpstorm.outsideClassFactory.make

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isCurrentClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.entities.DefinitionMethod
import com.github.ekvedaras.classfactoryphpstorm.entities.MakeMethodReference
import com.github.ekvedaras.classfactoryphpstorm.entities.StateMethodReferenceInsideFactory
import com.github.ekvedaras.classfactoryphpstorm.entities.StateMethodReferenceOutsideFactory
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4

class ClassFactoryPropertyMakeMethodTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u0313'
    }

    override fun getType(element: PsiElement): PhpType? {
        if (DumbService.isDumb(element.project)) return null

        if (element !is ArrayAccessExpression) return null
        if (element.firstPsiChild !is Variable) return null

        val key = element.childrenOfType<ArrayIndex>().firstOrNull()?.firstPsiChild ?: return null
        if (key !is StringLiteralExpression) return null

        val function = element.parentOfType<Function>() ?: return null
        if (function.parameters.isEmpty() || function.parameters[0].name != (element.firstPsiChild as Variable).name) return null

        if (function.parent.parent.parent !is ArrayHashElement && function.parent.parent.parent !is MethodReference) return null

        val methodReference = if (function.parent.parent.parent is ArrayHashElement) {
            val arrayHashElement = function.parent.parent.parent

            if (arrayHashElement !is ArrayHashElement) return null
            if (! function.parent.isArrayHashValueOf(arrayHashElement)) return null
            if (arrayHashElement.parent.parent.parent !is MethodReference) return null

            arrayHashElement.parentOfType<MethodReference>() ?: return null
        } else {
            function.parent.parent.parent as MethodReference
        }

        if (! methodReference.isClassFactoryMakeMethod()) return null

        val stateMethodReference = MakeMethodReference(methodReference)

        val definitionMethod = stateMethodReference.classFactory.definitionMethod ?: return null
        val propertyDefinition = definitionMethod.getPropertyDefinition(key.text.unquoteAndCleanup()) ?: return null

        if (propertyDefinition.value?.firstPsiChild !is PhpTypedElement) return null

        return (propertyDefinition.value?.firstPsiChild as PhpTypedElement).type
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