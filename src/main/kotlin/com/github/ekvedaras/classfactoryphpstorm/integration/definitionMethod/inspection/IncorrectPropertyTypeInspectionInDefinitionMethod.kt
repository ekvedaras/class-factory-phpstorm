package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.support.entities.DefinitionMethod
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class IncorrectPropertyTypeInspectionInDefinitionMethod : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpStringLiteralExpression(expression: StringLiteralExpression?) {
                if (expression == null) return

                if (DumbService.isDumb(expression.project)) return

                val arrayHashElement = expression.parent.parent
                if (arrayHashElement !is ArrayHashElement) return
                if (expression.isArrayHashValueOf(arrayHashElement)) return
                if (arrayHashElement.parent.parent !is PhpReturn) return

                val method = arrayHashElement.parentOfType<Method>() ?: return
                if (!method.isClassFactoryDefinition()) return

                val definitionMethod = DefinitionMethod(method)
                val targetClass = definitionMethod.classFactory.targetClass ?: return
                val property = targetClass.getPropertyByName(expression.text.unquoteAndCleanup()) ?: return
                val factoryValue = arrayHashElement.value ?: return
                if (factoryValue !is PhpTypedElement) return

                if (property.type != factoryValue.type) {
                    holder.registerProblem(
                        factoryValue,
                        MyBundle.message("incorrectPropertyType")
                            .replace("{property}", expression.text.unquoteAndCleanup())
                            .replace("{class}", targetClass.name),
                        ProblemHighlightType.WARNING,
                        TextRange(0, factoryValue.textLength)
                    )
                }
            }
        }
    }
}