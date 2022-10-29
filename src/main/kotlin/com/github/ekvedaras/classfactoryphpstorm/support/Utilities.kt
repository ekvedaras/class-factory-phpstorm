package com.github.ekvedaras.classfactoryphpstorm.support

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class Utilities private constructor() {
    companion object {
        fun String.unquoteAndCleanup() = this.replace("IntellijIdeaRulezzz", "").trim('\'', '"').trim()

        fun PhpClass.isClassFactory() =
            (this.extendsList.lastChild is ClassReference) && (this.extendsList.lastChild as ClassReference).name == "ClassFactory"

        // TODO There must be a better way
        fun PhpType.getFirstClass(project: Project) =
            this.types.firstOrNull { it.contains("#C") }?.substringAfter("#C")?.substringBefore('.')?.getClass(project)

        fun PhpType.isClassFactory(project: Project) =
            !this.isAmbiguous && this.getFirstClass(project)?.isClassFactory() == true

        fun Method.isClassFactoryDefinition() =
            this.name == "definition" && this.containingClass?.isClassFactory() ?: false

        fun MethodReference.isClassFactoryState() =
            this.name == "state" && this.parentOfType<Method>()?.containingClass?.isClassFactory() ?: false

        fun MethodReference.getActualClassReference(): ClassReference? = if (this.classReference is ClassReference) {
            this.classReference as ClassReference
        } else if (this.classReference is MethodReference && (this.classReference as MethodReference).firstPsiChild is ClassReference) {
            ((this.classReference as MethodReference).firstPsiChild as ClassReference)
        } else if (this.classReference is MethodReference && (this.classReference as MethodReference).firstPsiChild is MethodReference) {
            ((this.classReference as MethodReference).firstPsiChild as MethodReference).getActualClassReference()
        } else {
            null
        }

        fun MethodReference.isMostLikelyClassFactoryMakeMethod() =
            this.name == "make" && this.getActualClassReference()?.fqn?.endsWith("Factory") == true

        fun MethodReference.isMostLikelyClassFactoryStateMethod() =
            this.name == "state" && this.getActualClassReference()?.fqn?.endsWith("Factory") == true

        fun MethodReference.isClassFactoryMakeMethod() =
            this.name == "make" && this.getActualClassReference()?.getClass()?.isClassFactory() ?: false

        fun MethodReference.isClassFactoryStateMethod() =
            this.name == "state" && this.getActualClassReference()?.getClass()?.isClassFactory() ?: false

        fun PsiElement.isArrayHashValueOf(arrayHashElement: ArrayHashElement) = this == arrayHashElement.value

        fun String.getClass(project: Project) = PhpIndex
            .getInstance(project)
            .getClassesByFQN(this)
            .firstOrNull()

        fun ClassReference.getClass() = this.fqn?.getClass(this.project)

        fun Function.isShort(): Boolean = this.firstChild?.textMatches("fn") == true
        fun Variable.isNthFunctionParameter(function: Function, n: Int = 0): Boolean =
            function.parameters.size > n && function.parameters[n].name == this.name
    }
}