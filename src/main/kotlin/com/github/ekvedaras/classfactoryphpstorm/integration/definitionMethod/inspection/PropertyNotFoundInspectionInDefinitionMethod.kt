package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.domain.method.definition.ClassFactoryPropertyDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class PropertyNotFoundInspectionInDefinitionMethod : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpStringLiteralExpression(expression: StringLiteralExpression) {
                if (DumbService.isDumb(expression.project)) return

                val definition = try {
                    ClassFactoryPropertyDefinition(expression.parent.parent as? ArrayHashElement ?: return)
                } catch (e: DomainException) {
                    return
                }

                if (expression != definition.key) return

                if (
                    definition
                        .method
                        .classFactory
                        .targetClass
                        .getPropertyByName(definition.propertyName) == null
                ) {
                    holder.registerProblem(
                        definition.key,
                        MyBundle.message("classPropertyNotFound")
                            .replace("{property}", definition.propertyName)
                            .replace("{class}", definition.method.classFactory.targetClass.name),
                        ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
                        TextRange(1, definition.propertyName.length + 1)
                    )
                }
            }
        }
    }
}