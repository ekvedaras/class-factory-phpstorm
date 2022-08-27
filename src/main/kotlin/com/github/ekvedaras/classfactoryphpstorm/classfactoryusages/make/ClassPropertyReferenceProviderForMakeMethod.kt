package com.github.ekvedaras.classfactoryphpstorm.classfactoryusages.make

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.psireferences.ClassPropertyReference
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.entities.MakeMethodReference
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ClassPropertyReferenceProviderForMakeMethod : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val arrayHashElement = element.parent.parent
        if (arrayHashElement !is ArrayHashElement) return PsiReference.EMPTY_ARRAY
        if (element.isArrayHashValueOf(arrayHashElement)) return PsiReference.EMPTY_ARRAY
        if (arrayHashElement.parent.parent.parent !is MethodReference) return PsiReference.EMPTY_ARRAY

        val methodReference = arrayHashElement.parentOfType<MethodReference>() ?: return PsiReference.EMPTY_ARRAY
        if (! methodReference.isClassFactoryMakeMethod()) return PsiReference.EMPTY_ARRAY

        val makeMethodReference = MakeMethodReference(methodReference)
        val targetClass = makeMethodReference.classFactory.targetClass ?: return PsiReference.EMPTY_ARRAY

        return arrayOf(ClassPropertyReference(element as StringLiteralExpression, targetClass))
    }
}