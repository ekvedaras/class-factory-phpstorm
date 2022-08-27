package com.github.ekvedaras.classfactoryphpstorm

import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.*

class Utilities private constructor() {
    companion object {
        fun String.unquoteAndCleanup() = this.replace("IntellijIdeaRulezzz", "").trim('\'', '"').trim()
        fun PhpClass.isClassFactory() = (this.extendsList.lastChild as ClassReference).name == "ClassFactory"
        fun Method.isClassFactoryDefinition() = this.name == "definition" && this.containingClass?.isClassFactory() ?: false
        fun MethodReference.isClassFactoryState() = this.name == "state" && this.parentOfType<Method>()?.containingClass?.isClassFactory() ?: false
        fun MethodReference.isCurrentClassFactoryState() = this.isClassFactoryState() && this.classReference is Variable && this.classReference?.name == "this"
        fun PsiElement.isArrayHashValueOf(arrayHashElement: ArrayHashElement) = this == arrayHashElement.value
    }
}