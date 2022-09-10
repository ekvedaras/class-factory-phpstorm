package com.github.ekvedaras.classfactoryphpstorm.outsideClassFactory.state

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryStateMethod
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.entities.StateMethodReferenceOutsideFactory
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class PropertyNotFoundInspectionInStateMethod : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpStringLiteralExpression(expression: StringLiteralExpression?) {
                if (expression == null) return

                if (DumbService.isDumb(expression.project)) return

                val arrayHashElement = expression.parent.parent
                if (arrayHashElement !is ArrayHashElement) return
                if (expression.isArrayHashValueOf(arrayHashElement)) return
                if (arrayHashElement.parent.parent.parent !is MethodReference) return

                val methodReference = arrayHashElement.parentOfType<MethodReference>() ?: return
                if (!methodReference.isClassFactoryStateMethod()) return

                val makeMethodReference = StateMethodReferenceOutsideFactory(methodReference)
                val targetClass = makeMethodReference.classFactory.targetClass ?: return

                if (targetClass.constructor?.getParameterByName(expression.text.unquoteAndCleanup()) == null) {
                    holder.registerProblem(
                        expression,
                        MyBundle.message("classPropertyNotFound")
                            .replace("{property}", expression.text.replace("\'", ""))
                            .replace("{class}", targetClass.name),
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
                        TextRange(1, expression.textLength - 1)
                    )
                }
            }
        }
    }
}