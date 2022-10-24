package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.type.ClassFactoryPropertyDefinitionTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.type.ClassFactoryPropertyDefinitionTypeProvider.Companion.getClassFactoryDefinitionType
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.getClass
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactory
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.support.entities.ClassFactory
import com.github.ekvedaras.classfactoryphpstorm.support.entities.DefinitionMethod
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class IncorrectPropertyTypeInspectionForClosureReturnsInDefinitionMethod : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpReturn(expression: PhpReturn?) {
                if (expression == null) return

                if (DumbService.isDumb(expression.project)) return

                val function = expression.parentOfType<Function>() ?: return
                if (function.parent.parent.parent !is ArrayHashElement) return

                val arrayHashElement = function.parent.parent.parent

                if (arrayHashElement !is ArrayHashElement) return
                val key = arrayHashElement.key
                if (key !is StringLiteralExpression) return
                if (!function.parent.isArrayHashValueOf(arrayHashElement)) return
                if (arrayHashElement.parent.parent !is PhpReturn) return

                val method = arrayHashElement.parentOfType<Method>() ?: return
                if (!method.isClassFactoryDefinition()) return

                val definitionMethod = DefinitionMethod(method)
                val targetClass = definitionMethod.classFactory.targetClass ?: return
                val property = targetClass.getPropertyByName(key.text.unquoteAndCleanup()) ?: return
                val factoryValue = expression.firstPsiChild ?: return
                if (factoryValue !is PhpTypedElement) return
                val factoryValueType = factoryValue.getClassFactoryDefinitionType() ?: factoryValue.type

                val classFactoryUsed = factoryValueType.isClassFactory(expression.project)

                if ((classFactoryUsed && ClassFactory(factoryValueType.types.first().substringAfter("#C").substringBefore('.').getClass(expression.project) ?: return).targetClass?.type != property.type) || (!classFactoryUsed && property.type != factoryValueType)) {
                    holder.registerProblem(
                        factoryValue,
                        MyBundle.message("incorrectPropertyType")
                            .replace("{property}", key.text.unquoteAndCleanup())
                            .replace("{class}", targetClass.name),
                        ProblemHighlightType.WARNING,
                        TextRange(0, factoryValue.textLength)
                    )
                }
            }
        }
    }
}