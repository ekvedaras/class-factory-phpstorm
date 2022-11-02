package com.github.ekvedaras.classfactoryphpstorm.support

import com.github.ekvedaras.classfactoryphpstorm.domain.targetClass.TargetClass
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ClassPropertyReference(element: StringLiteralExpression, private val targetClass: TargetClass) :
    PsiReferenceBase<PsiElement>(element) {
    override fun resolve(): PsiElement? {
        rangeInElement = TextRange(1, element.textLength - 1)

        return targetClass.getPropertyByName(element.text.unquoteAndCleanup())?.psiElement
    }
}