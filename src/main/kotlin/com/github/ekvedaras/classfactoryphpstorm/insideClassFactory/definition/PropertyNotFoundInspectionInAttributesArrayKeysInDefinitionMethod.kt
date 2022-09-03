package com.github.ekvedaras.classfactoryphpstorm.insideClassFactory.definition

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.entities.DefinitionMethod
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.unquoteAndCleanup
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class PropertyNotFoundInspectionInAttributesArrayKeysInDefinitionMethod : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpStringLiteralExpression(expression: StringLiteralExpression?) {
                if (expression == null) return

                val attributesArray = expression.parent.parent

                if (expression.parent !is ArrayIndex) return
                if (attributesArray !is ArrayAccessExpression) return
                if (attributesArray.firstPsiChild !is Variable) return

                val function = attributesArray.parentOfType<Function>() ?: return
                if (function.parent.parent.parent !is ArrayHashElement) return
                if (function.parameters[0].name != (attributesArray.firstPsiChild as Variable).name) return

                val arrayHashElement = function.parent.parent.parent

                if (arrayHashElement !is ArrayHashElement) return
                if (! function.parent.isArrayHashValueOf(arrayHashElement)) return
                if (arrayHashElement.parent.parent !is PhpReturn) return

                val method = arrayHashElement.parentOfType<Method>() ?: return
                if (! method.isClassFactoryDefinition()) return

                val definitionMethod = DefinitionMethod(method)
                val targetClass = definitionMethod.classFactory.targetClass ?: return

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