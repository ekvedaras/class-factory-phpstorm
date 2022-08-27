package com.github.ekvedaras.classfactoryphpstorm.psireferences

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.entities.TargetClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ClassPropertyReference(element: StringLiteralExpression, private val targetClass: TargetClass) : PsiReferenceBase<PsiElement>(element) {
    override fun resolve(): PsiElement? {
        return targetClass.constructor?.getParameterByName(element.text.unquoteAndCleanup())?.parameter
    }
}