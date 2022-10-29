package com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.reference

import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
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
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ClassPropertyReferenceProviderForArrayKeysInDirectlyPassedClosure : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (DumbService.isDumb(element.project)) return PsiReference.EMPTY_ARRAY

        val arrayReturnedByClosure = element.parent.parent.parent

        if (element.parent.parent !is ArrayHashElement) return PsiReference.EMPTY_ARRAY
        if (arrayReturnedByClosure !is ArrayCreationExpression) return PsiReference.EMPTY_ARRAY

        val function = arrayReturnedByClosure.parentOfType<Function>() ?: return PsiReference.EMPTY_ARRAY
        if (function.parent.parent.parent !is MethodReference) return PsiReference.EMPTY_ARRAY
        val methodReference = function.parent.parent.parent as MethodReference

        val classFactoryMethodReference: ClassFactoryMethodReference = try {
            when (true) {
                methodReference.isClassFactoryState() -> StateMethodReferenceInsideFactory(methodReference)
                methodReference.isClassFactoryMakeMethod() -> MakeMethodReference(methodReference)
                methodReference.isClassFactoryStateMethod() -> StateMethodReferenceOutsideFactory(methodReference)
                else -> return PsiReference.EMPTY_ARRAY
            }
        } catch (e: DomainException) {
            return PsiReference.EMPTY_ARRAY
        }

        val targetClass = classFactoryMethodReference.classFactory.targetClass ?: return PsiReference.EMPTY_ARRAY

        return arrayOf(ClassPropertyReference(element as StringLiteralExpression, targetClass))
    }
}