package com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.reference

import com.github.ekvedaras.classfactoryphpstorm.domain.ClassFactory.Companion.asClassFactory
import com.github.ekvedaras.classfactoryphpstorm.domain.ClassFactoryMethodReference
import com.github.ekvedaras.classfactoryphpstorm.domain.closureState.AttributeAccess
import com.github.ekvedaras.classfactoryphpstorm.domain.method.make.MakeMethodReference
import com.github.ekvedaras.classfactoryphpstorm.domain.method.state.StateMethodReferenceInsideFactory
import com.github.ekvedaras.classfactoryphpstorm.domain.method.state.StateMethodReferenceOutsideFactory
import com.github.ekvedaras.classfactoryphpstorm.domain.targetClass.TargetClass
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.type.AttributesArrayValueTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.support.ClassPropertyReference
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactory
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryStateMethod
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.Variable

class ClassPropertyReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (DumbService.isDumb(element.project)) return PsiReference.EMPTY_ARRAY

        val arrayHashElement = element.parent.parent
        if (arrayHashElement !is ArrayHashElement) return PsiReference.EMPTY_ARRAY
        if (element.isArrayHashValueOf(arrayHashElement)) return PsiReference.EMPTY_ARRAY
        if (arrayHashElement.parent.parent.parent !is MethodReference) return PsiReference.EMPTY_ARRAY

        val methodReference = arrayHashElement.parentOfType<MethodReference>() ?: return PsiReference.EMPTY_ARRAY

        val targetClass : TargetClass = try {
            when (true) {
                methodReference.isClassFactoryState() -> StateMethodReferenceInsideFactory(methodReference).classFactory.targetClass
                methodReference.isClassFactoryMakeMethod() -> MakeMethodReference(methodReference).classFactory.targetClass
                methodReference.isClassFactoryStateMethod() -> StateMethodReferenceOutsideFactory(methodReference).classFactory.targetClass
                else -> {
                    if (methodReference.firstPsiChild !is ArrayAccessExpression) return PsiReference.EMPTY_ARRAY

                    try {
                        val attributeAccess = AttributeAccess(methodReference.firstPsiChild as ArrayAccessExpression)
                        val type = attributeAccess.getCompleteType(AttributesArrayValueTypeProvider())
                        if (! type.isClassFactory(element.project)) return PsiReference.EMPTY_ARRAY

                        type.asClassFactory(element.project)?.targetClass ?: return PsiReference.EMPTY_ARRAY
                    } catch (e: DomainException) { return PsiReference.EMPTY_ARRAY }
                }
            }
        } catch (e: DomainException) {
            return PsiReference.EMPTY_ARRAY
        }

        return arrayOf(ClassPropertyReference(element as StringLiteralExpression, targetClass))
    }
}