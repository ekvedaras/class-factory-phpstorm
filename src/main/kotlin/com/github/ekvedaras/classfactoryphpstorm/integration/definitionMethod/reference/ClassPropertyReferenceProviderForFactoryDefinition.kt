package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.reference

import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.entities.DefinitionMethod
import com.github.ekvedaras.classfactoryphpstorm.support.psiReferences.ClassPropertyReference
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ClassPropertyReferenceProviderForFactoryDefinition : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (DumbService.isDumb(element.project)) return PsiReference.EMPTY_ARRAY

        val arrayHashElement = element.parent.parent
        if (arrayHashElement !is ArrayHashElement) return PsiReference.EMPTY_ARRAY
        if (element.isArrayHashValueOf(arrayHashElement)) return PsiReference.EMPTY_ARRAY
        if (arrayHashElement.parent.parent !is PhpReturn) return PsiReference.EMPTY_ARRAY

        val method = arrayHashElement.parentOfType<Method>() ?: return PsiReference.EMPTY_ARRAY
        if (!method.isClassFactoryDefinition()) return PsiReference.EMPTY_ARRAY

        val definitionMethod = try { DefinitionMethod(method) } catch (e: DomainException) { return PsiReference.EMPTY_ARRAY }

        return arrayOf(ClassPropertyReference(element as StringLiteralExpression, definitionMethod.classFactory.targetClass))
    }
}