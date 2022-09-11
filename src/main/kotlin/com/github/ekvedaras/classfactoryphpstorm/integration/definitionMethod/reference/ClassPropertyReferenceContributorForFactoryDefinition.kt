package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ClassPropertyReferenceContributorForFactoryDefinition : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(StringLiteralExpression::class.java),
            ClassPropertyReferenceProviderForFactoryDefinition(),
        )

        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(StringLiteralExpression::class.java),
            ClassPropertyReferenceProviderForAttributesArrayKeysInFactoryDefinition(),
        )
    }
}