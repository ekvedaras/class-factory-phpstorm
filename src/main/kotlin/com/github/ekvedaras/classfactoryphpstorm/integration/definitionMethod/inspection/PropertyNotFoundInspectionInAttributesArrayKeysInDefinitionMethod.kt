package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.entities.AttributeAccess
import com.github.ekvedaras.classfactoryphpstorm.support.entities.ClosureDefinition.Companion.asClosureDefinition
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class PropertyNotFoundInspectionInAttributesArrayKeysInDefinitionMethod : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpStringLiteralExpression(expression: StringLiteralExpression) {
                if (DumbService.isDumb(expression.project)) return

                val attributeAccess = try {
                    AttributeAccess(expression.parent.parent as? ArrayAccessExpression ?: return)
                } catch (e: DomainException) {
                    return
                }

                val closureDefinition = attributeAccess.function.asClosureDefinition() ?: return

                if (
                    closureDefinition
                        .definition
                        .method
                        .classFactory
                        .targetClass
                        .getPropertyByName(attributeAccess.attributeName) == null
                ) {
                    holder.registerProblem(
                        expression,
                        MyBundle.message("classPropertyNotFound")
                            .replace("{property}", attributeAccess.attributeName)
                            .replace("{class}", closureDefinition.definition.method.classFactory.targetClass.name),
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
                        TextRange(1, expression.textLength - 1)
                    )
                }
            }
        }
    }
}