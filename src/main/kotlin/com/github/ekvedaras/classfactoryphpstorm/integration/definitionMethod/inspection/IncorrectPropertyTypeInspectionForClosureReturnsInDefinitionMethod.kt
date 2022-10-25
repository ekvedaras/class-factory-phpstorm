package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.getClass
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.getFirstClass
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactory
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.support.entities.AttributeAccess
import com.github.ekvedaras.classfactoryphpstorm.support.entities.ClassFactory
import com.github.ekvedaras.classfactoryphpstorm.support.entities.ClassFactoryPropertyDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.entities.DefinitionMethod
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class IncorrectPropertyTypeInspectionForClosureReturnsInDefinitionMethod : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpReturn(expression: PhpReturn?) {
                if (DumbService.isDumb(expression?.project ?: return)) return

                val attributeAccess = try {
                    AttributeAccess(expression.firstPsiChild as? ArrayAccessExpression ?: return)
                } catch (e: Exception) {
                    return
                }

                val definition = try {
                    ClassFactoryPropertyDefinition(attributeAccess.function.parent.parent.parent as? ArrayHashElement ?: return)
                } catch (e: Exception) {
                    return
                }

                if (!attributeAccess.function.parent.isArrayHashValueOf(definition.element)) return
                if (definition.element.parent.parent !is PhpReturn) return

                val method = definition.element.parentOfType<Method>() ?: return
                if (!method.isClassFactoryDefinition()) return

                val definitionMethod = DefinitionMethod(method)
                val targetClass = definitionMethod.classFactory.targetClass ?: return
                val property = targetClass.getPropertyByName(definition.propertyName) ?: return
                val factoryValueType = attributeAccess.getType()

                val classFactoryUsed = factoryValueType.isClassFactory(expression.project)

                if ((classFactoryUsed && ClassFactory(factoryValueType.getFirstClass(expression.project) ?: return).targetClass?.type != property.type) || (!classFactoryUsed && property.type != factoryValueType)) {
                    holder.registerProblem(
                        attributeAccess.element,
                        MyBundle.message("incorrectPropertyType")
                            .replace("{property}", definition.propertyName)
                            .replace("{class}", targetClass.name),
                        ProblemHighlightType.WARNING,
                        TextRange(0, attributeAccess.element.textLength)
                    )
                }
            }
        }
    }
}