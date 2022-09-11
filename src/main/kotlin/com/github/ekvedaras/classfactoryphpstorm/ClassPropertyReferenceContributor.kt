package com.github.ekvedaras.classfactoryphpstorm

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ClassPropertyReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(StringLiteralExpression::class.java),
            ClassPropertyReferenceProvider(),
        )

        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(StringLiteralExpression::class.java),
            ClassPropertyReferenceProviderForAttributesArrayKeys(),
        )
    }
}