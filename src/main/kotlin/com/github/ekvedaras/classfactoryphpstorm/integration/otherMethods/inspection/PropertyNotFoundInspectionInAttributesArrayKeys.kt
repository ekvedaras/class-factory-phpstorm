package com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.domain.ClassFactoryMethodReference
import com.github.ekvedaras.classfactoryphpstorm.domain.method.make.MakeMethodReference
import com.github.ekvedaras.classfactoryphpstorm.domain.method.state.StateMethodReferenceInsideFactory
import com.github.ekvedaras.classfactoryphpstorm.domain.method.state.StateMethodReferenceOutsideFactory
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryStateMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isNthFunctionParameter
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.ArrayIndex
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class PropertyNotFoundInspectionInAttributesArrayKeys : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpStringLiteralExpression(expression: StringLiteralExpression?) {
                if (expression == null) return

                if (DumbService.isDumb(expression.project)) return

                val attributesArray = expression.parent.parent

                if (expression.parent !is ArrayIndex) return
                if (attributesArray !is ArrayAccessExpression) return
                if (attributesArray.firstPsiChild !is Variable) return

                val function = attributesArray.parentOfType<Function>() ?: return
                if (!(attributesArray.firstPsiChild as Variable).isNthFunctionParameter(function)) return

                if (function.parent.parent.parent !is ArrayHashElement && function.parent.parent.parent !is MethodReference) return

                val methodReference = if (function.parent.parent.parent is ArrayHashElement) {
                    val arrayHashElement = function.parent.parent.parent

                    if (arrayHashElement !is ArrayHashElement) return
                    if (!function.parent.isArrayHashValueOf(arrayHashElement)) return
                    if (arrayHashElement.parent.parent.parent !is MethodReference) return

                    arrayHashElement.parentOfType() ?: return
                } else {
                    function.parent.parent.parent as MethodReference
                }

                val classFactoryMethodReference: ClassFactoryMethodReference = try {
                    when (true) {
                        methodReference.isClassFactoryState() -> StateMethodReferenceInsideFactory(methodReference)
                        methodReference.isClassFactoryMakeMethod() -> MakeMethodReference(methodReference)
                        methodReference.isClassFactoryStateMethod() -> StateMethodReferenceOutsideFactory(
                            methodReference
                        )

                        else -> return
                    }
                } catch (e: DomainException) {
                    return
                }

                val targetClass = classFactoryMethodReference.classFactory.targetClass

                if (targetClass.getPropertyByName(expression.text.unquoteAndCleanup()) == null) {
                    holder.registerProblem(
                        expression,
                        MyBundle.message("classPropertyNotFound")
                            .replace("{property}", expression.text.unquoteAndCleanup())
                            .replace("{class}", targetClass.name),
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
                        TextRange(1, expression.textLength - 1)
                    )
                }
            }
        }
    }
}