package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactory
import com.github.ekvedaras.classfactoryphpstorm.support.entities.ClassFactory.Companion.asClassFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class ReturnInClosureDefinition(val element: PhpReturn) {
    val value: PhpTypedElement = element.firstPsiChild as? PhpTypedElement
        ?: throw ReturnInClosureDefinitionException.valueIsNotTyped(element.firstPsiChild)
    val definition: ClassFactoryPropertyDefinition
    private val returnedAttributeAccess: AttributeAccess? = if (element.firstPsiChild is ArrayAccessExpression) {
        try {
            AttributeAccess(element.firstPsiChild as ArrayAccessExpression)
        } catch (e: DomainException) {
            null
        }
    } else {
        null
    }
    val type: PhpType?

    init {
        val arrayHashElement = element.parentOfType<Function>()?.parent?.parent?.parent
        if (arrayHashElement !is ArrayHashElement) {
            throw ReturnInClosureDefinitionException.parentArrayHashElementNotFound(arrayHashElement)
        }
        definition = ClassFactoryPropertyDefinition(arrayHashElement)
    }

    init {
        val resolvedType = returnedAttributeAccess?.getCompleteType() ?: value.type
        type = if (resolvedType.isClassFactory(element.project)) {
            resolvedType.asClassFactory(element.project)?.targetClass?.type
        } else {
            resolvedType
        }
    }
}

internal class ReturnInClosureDefinitionException(message: String) : DomainException(message) {
    companion object {
        fun valueIsNotTyped(given: PsiElement?) =
            ReturnInClosureDefinitionException("Returned value is not PhpTypedElement. Got ${given} instead.")

        fun parentArrayHashElementNotFound(given: PsiElement?) =
            ReturnInClosureDefinitionException("Parent ArrayHashElement not found in expected location. Got $given instead.")
    }
}