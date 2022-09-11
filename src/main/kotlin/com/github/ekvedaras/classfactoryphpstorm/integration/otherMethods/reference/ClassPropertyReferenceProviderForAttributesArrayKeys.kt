package com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.reference

import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryStateMethod
import com.github.ekvedaras.classfactoryphpstorm.support.entities.ClassFactoryMethodReference
import com.github.ekvedaras.classfactoryphpstorm.support.entities.MakeMethodReference
import com.github.ekvedaras.classfactoryphpstorm.support.entities.StateMethodReferenceInsideFactory
import com.github.ekvedaras.classfactoryphpstorm.support.entities.StateMethodReferenceOutsideFactory
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
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.Variable

class ClassPropertyReferenceProviderForAttributesArrayKeys : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (DumbService.isDumb(element.project)) return PsiReference.EMPTY_ARRAY

        val attributesArray = element.parent.parent

        if (element.parent !is ArrayIndex) return PsiReference.EMPTY_ARRAY
        if (attributesArray !is ArrayAccessExpression) return PsiReference.EMPTY_ARRAY
        if (attributesArray.firstPsiChild !is Variable) return PsiReference.EMPTY_ARRAY

        val function = attributesArray.parentOfType<Function>() ?: return PsiReference.EMPTY_ARRAY
        if (function.parameters.isEmpty() || function.parameters[0].name != (attributesArray.firstPsiChild as Variable).name) return PsiReference.EMPTY_ARRAY

        if (function.parent.parent.parent !is ArrayHashElement && function.parent.parent.parent !is MethodReference) return PsiReference.EMPTY_ARRAY

        val methodReference = if (function.parent.parent.parent is ArrayHashElement) {
            val arrayHashElement = function.parent.parent.parent

            if (arrayHashElement !is ArrayHashElement) return PsiReference.EMPTY_ARRAY
            if (!function.parent.isArrayHashValueOf(arrayHashElement)) return PsiReference.EMPTY_ARRAY
            if (arrayHashElement.parent.parent.parent !is MethodReference) return PsiReference.EMPTY_ARRAY

            arrayHashElement.parentOfType() ?: return PsiReference.EMPTY_ARRAY
        } else {
            function.parent.parent.parent as MethodReference
        }

        val classFactoryMethodReference: ClassFactoryMethodReference = when (true) {
            methodReference.isClassFactoryState() -> StateMethodReferenceInsideFactory(methodReference)
            methodReference.isClassFactoryMakeMethod() -> MakeMethodReference(methodReference)
            methodReference.isClassFactoryStateMethod() -> StateMethodReferenceOutsideFactory(methodReference)
            else -> return PsiReference.EMPTY_ARRAY
        }

        val targetClass = classFactoryMethodReference.classFactory.targetClass ?: return PsiReference.EMPTY_ARRAY

        return arrayOf(ClassPropertyReference(element as StringLiteralExpression, targetClass))
    }
}