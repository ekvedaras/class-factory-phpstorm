package com.github.ekvedaras.classfactoryphpstorm.entities

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.jetbrains.php.lang.psi.elements.Parameter

class TargetClassConstructorParameter(val parameter: Parameter, private val targetClass: TargetClass) {
    val lookup : LookupElement
        get() = LookupElementBuilder
            .createWithIcon(parameter)
            .withTypeText(parameter.type.toString())
}