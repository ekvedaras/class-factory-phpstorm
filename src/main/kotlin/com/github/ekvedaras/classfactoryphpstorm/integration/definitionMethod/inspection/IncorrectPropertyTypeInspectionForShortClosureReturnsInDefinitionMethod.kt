package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.domain.method.definition.ClosureDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.includes
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isShort
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class IncorrectPropertyTypeInspectionForShortClosureReturnsInDefinitionMethod : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpFunction(expression: Function) {
                if (DumbService.isDumb(expression.project)) return
                if (!expression.isShort()) return

                val closure = try {
                    ClosureDefinition(expression)
                } catch (e: DomainException) {
                    return
                }

                val property = closure
                    .definition
                    .method
                    .classFactory
                    .targetClass
                    .getPropertyByName(closure.definition.propertyName) ?: return

                val closureType = closure.type()

                if (closureType != null && property.type.includes(closureType, expression.project)) {
                    holder.registerProblem(
                        closure.returnedValue ?: return,
                        MyBundle.message("incorrectPropertyType")
                            .replace("{property}", property.name)
                            .replace("{class}", closure.definition.method.classFactory.targetClass.name),
                        ProblemHighlightType.WARNING,
                        TextRange(0, closure.returnedValue.textLength)
                    )
                }
            }
        }
    }
}