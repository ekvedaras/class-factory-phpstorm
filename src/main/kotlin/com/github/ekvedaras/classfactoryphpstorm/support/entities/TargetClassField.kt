package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.Field

class TargetClassField(val field: Field, private val targetClass: TargetClass) :
    TargetClassParameter {
    override val lookup: LookupElement
        get() = PrioritizedLookupElement.withPriority(
            LookupElementBuilder
                .createWithIcon(this.field)
                .withTypeText(this.field.type.toString()),
            this.getPriority().toDouble()
        )

    override val name: String
        get() = this.field.name

    override val isOptional: Boolean
        get() = this.field.defaultValue != null

    override val psiElement: PsiElement
        get() = this.field

    override fun getPriority() = (targetClass.fields.size ?: 0) -
            (targetClass.fields.indexOf(this) ?: 0)
}