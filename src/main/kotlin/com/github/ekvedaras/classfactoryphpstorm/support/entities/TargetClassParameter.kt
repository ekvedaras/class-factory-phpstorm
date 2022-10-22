package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType

interface TargetClassParameter {
    val lookup: LookupElement
    val name: String
    val isOptional: Boolean
    val psiElement: PsiElement
    val type: PhpType
    fun getPriority(): Int
}