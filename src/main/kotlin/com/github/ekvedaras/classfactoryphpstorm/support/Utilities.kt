package com.github.ekvedaras.classfactoryphpstorm.support

import com.github.ekvedaras.classfactoryphpstorm.domain.ClassFactory.Companion.asClassFactory
import com.github.ekvedaras.classfactoryphpstorm.domain.closureState.AttributeAccess
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.type.AttributesArrayValueTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.classFactoryTargetOrSelf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unwrapClosureValue
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.resolve.types.PhpType

private const val CLASS_FACTORY_FQN = "\\EKvedaras\\ClassFactory\\ClassFactory"
private const val CLOSURE_VALUE_FQN = "\\EKvedaras\\ClassFactory\\ClosureValue"

class Utilities private constructor() {
    companion object {
        fun String.unquoteAndCleanup() = this.replace("IntellijIdeaRulezzz", "").trim('\'', '"').trim()

        fun PhpClass.isClassFactory() =
            (this.extendsList.lastChild is ClassReference) && (this.extendsList.lastChild as ClassReference).fqn == CLASS_FACTORY_FQN

        // TODO There must be a better way
        fun PhpType.getFirstClass(project: Project) =
            this.types.firstOrNull { it.contains("#C") }?.substringAfter("#C")?.substringBefore('.')?.getClass(project)

        fun PhpType.isClassFactory(project: Project) =
            !this.isAmbiguous && this.getFirstClass(project)?.isClassFactory() == true

        fun PhpType.classFactoryTargetOrSelf(project: Project) = this.asClassFactory(project)?.targetClass?.type ?: this

        fun PhpType.unwrapClosureValue(project: Project): PhpType = if (this.getFirstClass(project)?.fqn == CLOSURE_VALUE_FQN) {
            PhpType.CLOSURE
        } else {
            this
        }

        fun PhpType.includes(that: PhpType, project: Project): Boolean {
            return this.widenedTypes(withBoolean = false).intersect(
                that.global(project).widenedTypes(withBoolean = this.types.contains("\\bool")).toSet()
            ).isEmpty()
        }

        private fun PhpType.widenedTypes(withBoolean: Boolean) = this.types.map {when(true) {
            it.endsWith("[]") -> "\\array"
            (withBoolean && (it == "\\true" || it.equals("\\false"))) -> "\\bool"
            else -> it
        }}

        fun Method.isClassFactoryDefinition() =
            this.name == "definition" && this.containingClass?.isClassFactory() ?: false

        fun MethodReference.isClassFactoryState() =
            this.name == "state" && this.parentOfType<Method>()?.containingClass?.isClassFactory() ?: false && this.firstPsiChild is Variable && (this.firstPsiChild as Variable).name == "this"

        fun MethodReference.getActualClassReference(): ClassReference? = if (this.classReference is ClassReference) {
            this.classReference as ClassReference
        } else if (this.classReference is MethodReference && (this.classReference as MethodReference).firstPsiChild is ClassReference) {
            ((this.classReference as MethodReference).firstPsiChild as ClassReference)
        } else if (this.classReference is MethodReference && (this.classReference as MethodReference).firstPsiChild is MethodReference) {
            ((this.classReference as MethodReference).firstPsiChild as MethodReference).getActualClassReference()
        } else {
            null
        }

        fun PsiElement.firstArrayAccessExpressionDescendant() : ArrayAccessExpression? {
            val child = this.childrenOfType<ArrayAccessExpression>().firstOrNull()
            if (child != null) return child

            val methodReferences = this.childrenOfType<MethodReference>()

            return methodReferences
                .firstOrNull { it.firstArrayAccessExpressionDescendant() != null }
                ?.firstArrayAccessExpressionDescendant()

        }

        fun MethodReference.isMostLikelyClassFactoryMakeMethod() =
            this.name == "make" && this.getActualClassReference()?.fqn?.endsWith("Factory") == true

        fun MethodReference.isMostLikelyClassFactoryStateMethod() =
            this.name == "state" && this.getActualClassReference()?.fqn?.endsWith("Factory") == true

        fun MethodReference.isClassFactoryMakeMethod() =
            this.name == "make" && this.isMemberOfAny(CLASS_FACTORY_FQN)

        fun MethodReference.getClassFactoryClass(): PhpClass? =
            if (DumbService.isDumb(project)) null
            else if (classReference !is PhpTypedElement) null
            else PhpIndex.getInstance(project)
                .completeType(project, (classReference as PhpTypedElement).type, mutableSetOf())
                .types
                .flatMap { PhpIndex.getInstance(project).getClassesByFQN(it) }
                .firstOrNull { it.isChildOfAny(CLASS_FACTORY_FQN, orIsAny = false) }

        fun MethodReference.isMemberOfAny(vararg classes: String): Boolean =
            if (DumbService.isDumb(project)) false
            else if (classReference !is PhpTypedElement) false
            else PhpIndex.getInstance(project)
                .completeType(project, (classReference as PhpTypedElement).type, mutableSetOf())
                .types
                .flatMap { PhpIndex.getInstance(project).getClassesByFQN(it) }
                .firstOrNull { it.isChildOfAny(*classes, orIsAny = false) } != null

        fun PhpClass.isChildOfAny(vararg superFqn: String, orIsAny: Boolean = false): Boolean = superFqn.contains(superFQN) || (orIsAny && superFqn.contains(fqn)) || superClass?.isChildOfAny(*superFqn, orIsAny = orIsAny) == true

        fun MethodReference.isClassFactoryStateMethod() =
            this.name == "state" && (this.isMemberOfAny(CLASS_FACTORY_FQN) || (
                this.firstPsiChild is ArrayAccessExpression
                        && AttributeAccess(this.firstPsiChild as ArrayAccessExpression)
                        .getCompleteType(AttributesArrayValueTypeProvider())
                        .isClassFactory(this.project))
            )

        fun PsiElement.isArrayHashValueOf(arrayHashElement: ArrayHashElement) = this == arrayHashElement.value

        fun String.getClass(project: Project) = PhpIndex
            .getInstance(project)
            .getClassesByFQN(this)
            .firstOrNull()

        fun ClassReference.getClass() = this.fqn?.getClass(this.project)

        fun Function.isShort(): Boolean = this.firstChild?.textMatches("fn") == true
        fun Function.returnedValue(): PsiElement? = if (this.isShort()) {
            this.lastChild
        } else {
            this.childrenOfType<GroupStatement>().firstOrNull()?.childrenOfType<PhpReturn>()?.firstOrNull()
        }

        fun Variable.isNthFunctionParameter(function: Function, n: Int = 0): Boolean =
            function.parameters.size > n && function.parameters[n].name == this.name
    }
}