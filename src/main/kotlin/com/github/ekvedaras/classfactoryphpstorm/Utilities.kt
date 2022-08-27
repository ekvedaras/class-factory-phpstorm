package com.github.ekvedaras.classfactoryphpstorm

import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.MemberReference
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass

class Utilities private constructor() {
    companion object {
        fun String.unquoteAndCleanup() = this.replace("IntellijIdeaRulezzz", "").trim('\'', '"').trim()
        fun PhpClass.isClassFactory() = (this.extendsList.lastChild as ClassReference).name == "ClassFactory"
        fun Method.isClassFactoryDefinition() = this.name == "definition" && this.containingClass?.isClassFactory() ?: false
        fun MethodReference.isClassFactoryState() = this.name == "state" && this.parentOfType<Method>()?.containingClass?.isClassFactory() ?: false
        fun PsiElement.isArrayHashValueOf(arrayHashElement: ArrayHashElement) = this == arrayHashElement.value
    }
}