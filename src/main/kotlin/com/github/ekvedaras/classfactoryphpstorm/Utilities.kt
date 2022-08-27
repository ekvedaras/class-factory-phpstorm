package com.github.ekvedaras.classfactoryphpstorm

import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.*

class Utilities private constructor() {
    companion object {
        fun String.unquoteAndCleanup() = this.replace("IntellijIdeaRulezzz", "").trim('\'', '"').trim()
        fun PhpClass.isClassFactory() = (this.extendsList.lastChild as ClassReference).name == "ClassFactory"
        fun Method.isClassFactoryDefinition() = this.name == "definition" && this.containingClass?.isClassFactory() ?: false
        fun MethodReference.isClassFactoryState() = this.name == "state" && this.parentOfType<Method>()?.containingClass?.isClassFactory() ?: false
        fun MethodReference.isCurrentClassFactoryState() = this.isClassFactoryState() && this.classReference is Variable && this.classReference?.name == "this"
        fun MethodReference.getActualClassReference() : ClassReference? = if (this.classReference is ClassReference) {
            this.classReference as ClassReference
        } else if (this.classReference is MethodReference && (this.classReference as MethodReference).firstPsiChild is ClassReference) {
            ((this.classReference as MethodReference).firstPsiChild as ClassReference)
        } else if (this.classReference is MethodReference && (this.classReference as MethodReference).firstPsiChild is MethodReference) {
            ((this.classReference as MethodReference).firstPsiChild as MethodReference).getActualClassReference()
        } else {
            null
        }
        fun MethodReference.isClassFactoryMakeMethod() = this.name == "make" && this.getActualClassReference()?.getClass()?.isClassFactory() ?: false
        fun MethodReference.isClassFactoryStateMethod() = this.name == "state" && this.getActualClassReference()?.getClass()?.isClassFactory() ?: false
        fun PsiElement.isArrayHashValueOf(arrayHashElement: ArrayHashElement) = this == arrayHashElement.value
        fun ClassReference.getClass() = PhpIndex
            .getInstance(this.project)
            .getClassesByFQN(this.fqn)
            .firstOrNull()
    }
}