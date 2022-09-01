package com.github.ekvedaras.classfactoryphpstorm.insideClassFactory.definition

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.psiReferences.ClassPropertyReference
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.entities.DefinitionMethod
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.Function

class ClassPropertyReferenceProviderForAttributesArrayKeysInFactoryDefinition : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val attributesArray = element.parent.parent

        if (element.parent !is ArrayIndex) return PsiReference.EMPTY_ARRAY
        if (attributesArray !is ArrayAccessExpression) return PsiReference.EMPTY_ARRAY
        if (attributesArray.firstPsiChild !is Variable) return PsiReference.EMPTY_ARRAY

        val function = attributesArray.parentOfType<Function>() ?: return PsiReference.EMPTY_ARRAY
        if (function.parent.parent.parent !is ArrayHashElement) return PsiReference.EMPTY_ARRAY
        if (function.parameters[0].name != (attributesArray.firstPsiChild as Variable).name) return PsiReference.EMPTY_ARRAY

        val arrayHashElement = function.parent.parent.parent

        if (arrayHashElement !is ArrayHashElement) return PsiReference.EMPTY_ARRAY
        if (! function.parent.isArrayHashValueOf(arrayHashElement)) return PsiReference.EMPTY_ARRAY
        if (arrayHashElement.parent.parent !is PhpReturn) return PsiReference.EMPTY_ARRAY

        val method = arrayHashElement.parentOfType<Method>() ?: return PsiReference.EMPTY_ARRAY
        if (! method.isClassFactoryDefinition()) return PsiReference.EMPTY_ARRAY

        val definitionMethod = DefinitionMethod(method)
        val targetClass = definitionMethod.classFactory.targetClass ?: return PsiReference.EMPTY_ARRAY

        return arrayOf(ClassPropertyReference(element as StringLiteralExpression, targetClass))
    }
}