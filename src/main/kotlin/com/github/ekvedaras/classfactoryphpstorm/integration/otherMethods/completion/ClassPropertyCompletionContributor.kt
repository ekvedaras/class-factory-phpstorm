package com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.lexer.PhpTokenTypes

class ClassPropertyCompletionContributor : CompletionContributor() {
    override fun invokeAutoPopup(position: PsiElement, typeChar: Char): Boolean {
        return typeChar == '\''
    }

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(PhpTokenTypes.STRING_LITERAL_SINGLE_QUOTE),
            ClassPropertyCompletionProvider(),
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(PhpTokenTypes.STRING_LITERAL_SINGLE_QUOTE),
            ClassPropertyCompletionProviderForAttributesArrayInClosure(),
        )
    }
}