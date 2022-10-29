package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.entities.AttributeAccess
import com.github.ekvedaras.classfactoryphpstorm.support.entities.ClassFactory.Companion.asClassFactory
import com.github.ekvedaras.classfactoryphpstorm.support.entities.ClassFactoryPropertyDefinition
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class IncorrectPropertyTypeInspectionForClosureReturnsInDefinitionMethod : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpReturn(expression: PhpReturn?) {
                if (DumbService.isDumb(expression?.project ?: return)) return

                val attributeAccess = try {
                    AttributeAccess(expression.firstPsiChild as? ArrayAccessExpression ?: return)
                } catch (e: DomainException) {
                    return
                }

                val definition = try {
                    ClassFactoryPropertyDefinition(
                        attributeAccess.function.parent.parent.parent as? ArrayHashElement ?: return
                    )
                } catch (e: DomainException) {
                    return
                }

                if (!attributeAccess.function.parent.isArrayHashValueOf(definition.element)) return

                val property =
                    definition.method.classFactory.targetClass.getPropertyByName(definition.propertyName) ?: return

                if (property.type != (attributeAccess.getCompleteType()
                        .asClassFactory(expression.project)?.targetClass?.type ?: attributeAccess.getCompleteType())
                ) {
                    holder.registerProblem(
                        attributeAccess.element,
                        MyBundle.message("incorrectPropertyType")
                            .replace("{property}", property.name)
                            .replace("{class}", definition.method.classFactory.targetClass.name),
                        ProblemHighlightType.WARNING,
                        TextRange(0, attributeAccess.element.textLength)
                    )
                }
            }
        }
    }
}