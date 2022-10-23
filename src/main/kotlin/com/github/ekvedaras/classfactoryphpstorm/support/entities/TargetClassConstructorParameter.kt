package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.jetbrains.php.lang.psi.elements.Parameter
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class TargetClassConstructorParameter(val parameter: Parameter, private val targetClass: TargetClass) :
    TargetClassParameter {
    override val lookup: LookupElement
        get() = PrioritizedLookupElement.withPriority(
            LookupElementBuilder
                .createWithIcon(parameter)
                .withTypeText(parameter.type.toString()),
            this.getPriority().toDouble()
        )

    override val name: String
        get() = parameter.name

    override val isOptional: Boolean
        get() = parameter.isOptional

    override val psiElement: Parameter
        get() = this.parameter

    override val type: PhpType
        get() = this.parameter.type

    override fun getPriority() = (targetClass.constructor?.totalParameters ?: 0) -
            (targetClass.constructor?.getParameterIndex(this) ?: 0)
}