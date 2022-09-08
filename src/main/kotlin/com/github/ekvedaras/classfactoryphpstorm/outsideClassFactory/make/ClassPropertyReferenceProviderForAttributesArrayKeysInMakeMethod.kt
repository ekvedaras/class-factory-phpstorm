package com.github.ekvedaras.classfactoryphpstorm.outsideClassFactory.make

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.psiReferences.ClassPropertyReference
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.entities.MakeMethodReference
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.Function

class ClassPropertyReferenceProviderForAttributesArrayKeysInMakeMethod : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (DumbService.isDumb(element.project)) return PsiReference.EMPTY_ARRAY

        val attributesArray = element.parent.parent

        if (element.parent !is ArrayIndex) return PsiReference.EMPTY_ARRAY
        if (attributesArray !is ArrayAccessExpression) return PsiReference.EMPTY_ARRAY
        if (attributesArray.firstPsiChild !is Variable) return PsiReference.EMPTY_ARRAY

        val function = attributesArray.parentOfType<Function>() ?: return PsiReference.EMPTY_ARRAY
        if (function.parameters[0].name != (attributesArray.firstPsiChild as Variable).name) return PsiReference.EMPTY_ARRAY

        if (function.parent.parent.parent !is ArrayHashElement && function.parent.parent.parent !is MethodReference) return PsiReference.EMPTY_ARRAY

        val methodReference = if (function.parent.parent.parent is ArrayHashElement) {
            val arrayHashElement = function.parent.parent.parent

            if (arrayHashElement !is ArrayHashElement) return PsiReference.EMPTY_ARRAY
            if (! function.parent.isArrayHashValueOf(arrayHashElement)) return PsiReference.EMPTY_ARRAY
            if (arrayHashElement.parent.parent.parent !is MethodReference) return PsiReference.EMPTY_ARRAY

            arrayHashElement.parentOfType<MethodReference>() ?: return PsiReference.EMPTY_ARRAY
        } else {
            function.parent.parent.parent as MethodReference
        }

        if (! methodReference.isClassFactoryMakeMethod()) return PsiReference.EMPTY_ARRAY

        val makeMethodReference = MakeMethodReference(methodReference)
        val targetClass = makeMethodReference.classFactory.targetClass ?: return PsiReference.EMPTY_ARRAY

        return arrayOf(ClassPropertyReference(element as StringLiteralExpression, targetClass))
    }
}