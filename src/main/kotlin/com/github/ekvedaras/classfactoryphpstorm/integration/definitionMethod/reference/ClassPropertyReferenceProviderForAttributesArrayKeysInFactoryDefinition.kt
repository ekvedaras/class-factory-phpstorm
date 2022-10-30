package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.reference

import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.entities.AttributeAccess
import com.github.ekvedaras.classfactoryphpstorm.support.entities.ClosureDefinition.Companion.asClosureDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.psiReferences.ClassPropertyReference
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ClassPropertyReferenceProviderForAttributesArrayKeysInFactoryDefinition : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (DumbService.isDumb(element.project)) return PsiReference.EMPTY_ARRAY

        val attributeAccess = try {
            AttributeAccess(element.parent.parent as? ArrayAccessExpression ?: return PsiReference.EMPTY_ARRAY)
        } catch (e: DomainException) {
            return PsiReference.EMPTY_ARRAY
        }

        val closureDefinition = attributeAccess.function.asClosureDefinition() ?: return PsiReference.EMPTY_ARRAY

        return arrayOf(
            ClassPropertyReference(
                element as StringLiteralExpression,
                closureDefinition.definition.method.classFactory.targetClass
            )
        )
    }
}