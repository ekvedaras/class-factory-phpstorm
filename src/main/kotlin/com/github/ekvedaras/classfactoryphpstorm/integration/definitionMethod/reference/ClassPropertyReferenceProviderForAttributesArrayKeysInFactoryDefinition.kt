package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.reference

import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isNthFunctionParameter
import com.github.ekvedaras.classfactoryphpstorm.support.entities.DefinitionMethod
import com.github.ekvedaras.classfactoryphpstorm.support.psiReferences.ClassPropertyReference
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.ArrayIndex
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.Variable

class ClassPropertyReferenceProviderForAttributesArrayKeysInFactoryDefinition : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (DumbService.isDumb(element.project)) return PsiReference.EMPTY_ARRAY

        val attributesArray = element.parent.parent

        if (element.parent !is ArrayIndex) return PsiReference.EMPTY_ARRAY
        if (attributesArray !is ArrayAccessExpression) return PsiReference.EMPTY_ARRAY
        if (attributesArray.firstPsiChild !is Variable) return PsiReference.EMPTY_ARRAY

        val function = attributesArray.parentOfType<Function>() ?: return PsiReference.EMPTY_ARRAY
        if (function.parent.parent.parent !is ArrayHashElement) return PsiReference.EMPTY_ARRAY
        if (!(attributesArray.firstPsiChild as Variable).isNthFunctionParameter(function)) return PsiReference.EMPTY_ARRAY

        val arrayHashElement = function.parent.parent.parent

        if (arrayHashElement !is ArrayHashElement) return PsiReference.EMPTY_ARRAY
        if (!function.parent.isArrayHashValueOf(arrayHashElement)) return PsiReference.EMPTY_ARRAY
        if (arrayHashElement.parent.parent !is PhpReturn) return PsiReference.EMPTY_ARRAY

        val method = arrayHashElement.parentOfType<Method>() ?: return PsiReference.EMPTY_ARRAY
        if (!method.isClassFactoryDefinition()) return PsiReference.EMPTY_ARRAY

        val definitionMethod = try {
            DefinitionMethod(method)
        } catch (e: DomainException) {
            return PsiReference.EMPTY_ARRAY
        }

        return arrayOf(
            ClassPropertyReference(
                element as StringLiteralExpression,
                definitionMethod.classFactory.targetClass
            )
        )
    }
}