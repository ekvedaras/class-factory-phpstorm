package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement

interface TargetClassParameter {
    val lookup: LookupElement
    val name: String
    val isOptional: Boolean
    val psiElement: PsiElement
    fun getPriority(): Int
}