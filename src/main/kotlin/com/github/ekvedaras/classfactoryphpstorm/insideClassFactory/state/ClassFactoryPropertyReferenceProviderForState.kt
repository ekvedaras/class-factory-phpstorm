package com.github.ekvedaras.classfactoryphpstorm.insideClassFactory.state

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isCurrentClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.entities.StateMethodReferenceInsideFactory
import com.github.ekvedaras.classfactoryphpstorm.psiReferences.ClassPropertyReference
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.*

class ClassFactoryPropertyReferenceProviderForState : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val array = element.parent.parent
        if (array !is ArrayHashElement) return PsiReference.EMPTY_ARRAY
        if (element.isArrayHashValueOf(array)) return PsiReference.EMPTY_ARRAY
        if (array.parent.parent.parent !is MethodReference) return PsiReference.EMPTY_ARRAY

        val methodReference = array.parentOfType<MethodReference>() ?: return PsiReference.EMPTY_ARRAY
        if (! methodReference.isCurrentClassFactoryState()) return PsiReference.EMPTY_ARRAY

        val stateMethodReference = StateMethodReferenceInsideFactory(methodReference)
        val targetClass = stateMethodReference.classFactory.targetClass ?: return PsiReference.EMPTY_ARRAY

        return arrayOf(ClassPropertyReference(element as StringLiteralExpression, targetClass))
    }
}