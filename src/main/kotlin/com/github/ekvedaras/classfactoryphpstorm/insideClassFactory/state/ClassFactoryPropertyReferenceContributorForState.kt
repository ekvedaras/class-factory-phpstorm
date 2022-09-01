package com.github.ekvedaras.classfactoryphpstorm.insideClassFactory.state

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ClassFactoryPropertyReferenceContributorForState : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(StringLiteralExpression::class.java),
            ClassFactoryPropertyReferenceProviderForState(),
        )

        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(StringLiteralExpression::class.java),
            ClassFactoryPropertyReferenceProviderForAttributesArrayKeysInState(),
        )
    }
}