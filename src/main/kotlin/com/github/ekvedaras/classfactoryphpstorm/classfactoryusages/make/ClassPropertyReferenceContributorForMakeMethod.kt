package com.github.ekvedaras.classfactoryphpstorm.classfactoryusages.make

import com.github.ekvedaras.classfactoryphpstorm.classfactory.definition.ClassPropertyReferenceProviderForFactoryDefinition
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ClassPropertyReferenceContributorForMakeMethod : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(StringLiteralExpression::class.java),
            ClassPropertyReferenceProviderForMakeMethod(),
        )
    }
}