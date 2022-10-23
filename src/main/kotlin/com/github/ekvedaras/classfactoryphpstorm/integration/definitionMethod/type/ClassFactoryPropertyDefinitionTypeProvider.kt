package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.type

import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.support.entities.DefinitionMethod
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
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.Variable
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
        if (function.parameters.isEmpty() || function.parameters[0].name != (element.firstPsiChild as Variable).name) return null

        val arrayHashElement = function.parent.parent.parent

        if (arrayHashElement !is ArrayHashElement) return null
        if (!function.parent.isArrayHashValueOf(arrayHashElement)) return null

        if (arrayHashElement.parent.parent !is PhpReturn) return null

        val method = arrayHashElement.parentOfType<Method>() ?: return null
        if (!method.isClassFactoryDefinition()) return null

        val definitionMethod = DefinitionMethod(method)
        val propertyDefinition =
            definitionMethod.getPropertyDefinition(key.text.unquoteAndCleanup())?.value ?: return null

        if (propertyDefinition.firstPsiChild is Function) {
            val propertyDefinitionFunction = propertyDefinition.firstPsiChild as Function
            if (propertyDefinitionFunction.type.filterMixed() != PhpType.EMPTY) {
                return propertyDefinitionFunction.type.filterMixed()
            }

            if (propertyDefinitionFunction.parameters.isEmpty()) return null

            return when (propertyDefinitionFunction.firstPsiChild?.text) {
                "function" -> this.resolveClosureType(propertyDefinitionFunction)
                "fn" -> this.resolveShortClosureType(propertyDefinitionFunction)
                else -> null
            }
        }

        if (propertyDefinition !is PhpTypedElement) return null

        return propertyDefinition.type
    }

    private fun resolveClosureType(function: Function): PhpType? {
        return this.getType(
            function
                .childrenOfType<GroupStatement>()
                .firstOrNull()
                ?.childrenOfType<PhpReturn>()
                ?.filterNot { it.childrenOfType<ArrayAccessExpression>().isEmpty() }
                ?.firstOrNull {
                    it.childrenOfType<ArrayAccessExpression>()
                        .firstOrNull()?.firstPsiChild is Variable && (it.childrenOfType<ArrayAccessExpression>()
                        .firstOrNull()?.firstPsiChild as Variable).name == function.getParameter(0)?.name
                } ?: return null
        )
    }

    private fun resolveShortClosureType(function: Function): PhpType? {
        return this.getType(
            function
                .childrenOfType<ArrayAccessExpression>()
                .firstOrNull {
                    it.firstPsiChild is Variable && (it.firstPsiChild as Variable).name == function.getParameter(0)?.name
                } ?: return null
        )
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