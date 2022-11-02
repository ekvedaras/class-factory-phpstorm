package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.domain.method.definition.ClassFactoryPropertyDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.classFactoryTargetOrSelf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unwrapClosureValue
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class IncorrectPropertyTypeInspectionInDefinitionMethod : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpStringLiteralExpression(expression: StringLiteralExpression) {
                if (DumbService.isDumb(expression.project)) return

                val definition = try {
                    ClassFactoryPropertyDefinition(expression.parent.parent as? ArrayHashElement ?: return)
                } catch (e: DomainException) {
                    return
                }

                if (definition.value.type == PhpType.STRING && expression != definition.value) return
                if (definition.isClosure()) return

                val property =
                    definition.method.classFactory.targetClass.getPropertyByName(definition.propertyName) ?: return

                if (property.type.types.intersect(
                        definition.typeForDefinition()
                            .classFactoryTargetOrSelf(expression.project)
                            .unwrapClosureValue(expression.project)
                            .global(expression.project)
                            .types
                ).isEmpty()) {
                    holder.registerProblem(
                        definition.value,
                        MyBundle.message("incorrectPropertyType")
                            .replace("{property}", definition.propertyName)
                            .replace("{class}", definition.method.classFactory.targetClass.name),
                        ProblemHighlightType.WARNING,
                        TextRange(0, definition.value.textLength)
                    )
                }
            }
        }
    }
}