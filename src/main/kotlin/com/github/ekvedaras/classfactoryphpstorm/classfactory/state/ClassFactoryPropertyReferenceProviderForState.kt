package com.github.ekvedaras.classfactoryphpstorm.classfactory.state

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.entities.StateMethodReference
import com.github.ekvedaras.classfactoryphpstorm.psireferences.ClassPropertyReference
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ClassFactoryPropertyReferenceProviderForState : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val array = element.parent.parent
        if (array !is ArrayHashElement) return PsiReference.EMPTY_ARRAY
        if (element.isArrayHashValueOf(array)) return PsiReference.EMPTY_ARRAY

        val methodReference = array.parentOfType<MethodReference>() ?: return PsiReference.EMPTY_ARRAY
        if (! methodReference.isClassFactoryState()) return PsiReference.EMPTY_ARRAY

        val stateMethodReference = StateMethodReference(methodReference)
        val targetClass = stateMethodReference.classFactory.targetClass ?: return PsiReference.EMPTY_ARRAY

        return arrayOf(ClassPropertyReference(element as StringLiteralExpression, targetClass))
    }
}