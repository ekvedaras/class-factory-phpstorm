package com.github.ekvedaras.classfactoryphpstorm.classfactory.definition

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.psireferences.ClassPropertyReference
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.entities.DefinitionMethod
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ClassPropertyReferenceProviderForFactoryDefinition : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val arrayHashElement = element.parent.parent
        if (arrayHashElement !is ArrayHashElement) return PsiReference.EMPTY_ARRAY
        if (element.isArrayHashValueOf(arrayHashElement)) return PsiReference.EMPTY_ARRAY
        if (arrayHashElement.parent.parent !is PhpReturn) return PsiReference.EMPTY_ARRAY

        val method = arrayHashElement.parentOfType<Method>() ?: return PsiReference.EMPTY_ARRAY
        if (! method.isClassFactoryDefinition()) return PsiReference.EMPTY_ARRAY

        val definitionMethod = DefinitionMethod(method)
        val targetClass = definitionMethod.classFactory.targetClass ?: return PsiReference.EMPTY_ARRAY

        return arrayOf(ClassPropertyReference(element as StringLiteralExpression, targetClass))
    }
}