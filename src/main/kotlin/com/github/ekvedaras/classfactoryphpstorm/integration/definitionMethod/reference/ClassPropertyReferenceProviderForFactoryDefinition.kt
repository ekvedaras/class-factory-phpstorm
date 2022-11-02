package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.reference

import com.github.ekvedaras.classfactoryphpstorm.domain.method.definition.ClassFactoryPropertyDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.ClassPropertyReference
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ClassPropertyReferenceProviderForFactoryDefinition : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (DumbService.isDumb(element.project)) return PsiReference.EMPTY_ARRAY

        val definition = try {
            ClassFactoryPropertyDefinition(
                element.parent.parent as? ArrayHashElement ?: return PsiReference.EMPTY_ARRAY
            )
        } catch (e: DomainException) {
            return PsiReference.EMPTY_ARRAY
        }

        if (element != definition.key) return PsiReference.EMPTY_ARRAY

        return arrayOf(
            ClassPropertyReference(
                element as StringLiteralExpression,
                definition.method.classFactory.targetClass
            )
        )
    }
}