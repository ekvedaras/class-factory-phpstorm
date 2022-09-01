package com.github.ekvedaras.classfactoryphpstorm.insideClassFactory.definition

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.entities.DefinitionMethod
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4

class ClassFactoryPropertyDefinitionTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u0310'
    }

    override fun getType(element: PsiElement): PhpType? {
        if (DumbService.isDumb(element.project)) return null

        if (element !is ArrayAccessExpression) return null
        if (element.firstPsiChild !is Variable) return null

        val key = element.childrenOfType<ArrayIndex>().firstOrNull()?.firstPsiChild ?: return null
        if (key !is StringLiteralExpression) return null

        val function = element.parentOfType<Function>() ?: return null
        if (function.parent.parent.parent !is ArrayHashElement) return null
        if (function.parameters[0].name != (element.firstPsiChild as Variable).name) return null

        val arrayHashElement = function.parent.parent.parent

        if (arrayHashElement !is ArrayHashElement) return null
        if (! function.parent.isArrayHashValueOf(arrayHashElement)) return null

        if (arrayHashElement.parent.parent !is PhpReturn) return null

        val method = arrayHashElement.parentOfType<Method>() ?: return null
        if (! method.isClassFactoryDefinition()) return null

        val definitionMethod = DefinitionMethod(method)
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