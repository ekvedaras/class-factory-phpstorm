package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.domain.method.definition.ReturnInClosureDefinition
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class IncorrectPropertyTypeInspectionForClosureReturnsInDefinitionMethod : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpReturn(expression: PhpReturn?) {
                if (DumbService.isDumb(expression?.project ?: return)) return

                val returnInClosure = try {
                    ReturnInClosureDefinition(expression)
                } catch (e: DomainException) {
                    return
                }

                val property = returnInClosure
                    .definition
                    .method
                    .classFactory
                    .targetClass
                    .getPropertyByName(returnInClosure.definition.propertyName) ?: return

                if (property.type != returnInClosure.type) {
                    holder.registerProblem(
                        returnInClosure.value,
                        MyBundle.message("incorrectPropertyType")
                            .replace("{property}", property.name)
                            .replace("{class}", returnInClosure.definition.method.classFactory.targetClass.name),
                        ProblemHighlightType.WARNING,
                        TextRange(0, returnInClosure.value.textLength)
                    )
                }
            }
        }
    }
}