package com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.domain.ClassFactory
import com.github.ekvedaras.classfactoryphpstorm.domain.ClassFactoryMethodReference
import com.github.ekvedaras.classfactoryphpstorm.domain.method.make.MakeMethodReference
import com.github.ekvedaras.classfactoryphpstorm.domain.method.state.StateMethodReferenceInsideFactory
import com.github.ekvedaras.classfactoryphpstorm.domain.method.state.StateMethodReferenceOutsideFactory
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.type.AttributesArrayValueTypeProvider.Companion.getClassFactoryStateType
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.getClass
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactory
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryStateMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unwrapClosureValue
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class IncorrectPropertyTypeInspectionForClosureReturns : PhpInspection() {
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
                if (arrayHashElement.parent.parent.parent !is MethodReference) return

                val methodReference = arrayHashElement.parentOfType<MethodReference>() ?: return

                val classFactoryMethodReference: ClassFactoryMethodReference = try {
                    when (true) {
                        methodReference.isClassFactoryState() -> StateMethodReferenceInsideFactory(methodReference)
                        methodReference.isClassFactoryMakeMethod() -> MakeMethodReference(methodReference)
                        methodReference.isClassFactoryStateMethod() -> StateMethodReferenceOutsideFactory(
                            methodReference
                        )

                        else -> return
                    }
                } catch (e: DomainException) {
                    return
                }

                val targetClass = classFactoryMethodReference.classFactory.targetClass
                val property = targetClass.getPropertyByName(key.text.unquoteAndCleanup()) ?: return

                val stateValue = if (expression.firstPsiChild is MethodReference) {
                    (expression.firstPsiChild as MethodReference).firstPsiChild
                } else {
                    expression.firstPsiChild
                } ?: return

                if (stateValue !is PhpTypedElement) return

                val stateValueType = stateValue.getClassFactoryStateType() ?: stateValue.type.unwrapClosureValue(expression.project)

                val factoryDefinitionValueType = classFactoryMethodReference
                    .classFactory
                    .definitionMethod
                    .getPropertyDefinition(property.name)
                    ?.typeForDefinition()

                val classFactoryUsed = stateValueType.isClassFactory(expression.project)

                if ((classFactoryUsed && ClassFactory(
                        stateValueType.types.first().substringAfter("#C").substringBefore('.')
                            .getClass(expression.project) ?: return
                    ).targetClass.type != property.type) || (!classFactoryUsed && factoryDefinitionValueType != null && stateValueType.types.intersect(factoryDefinitionValueType.global(expression.project).types).isEmpty())
                ) {
                    holder.registerProblem(
                        stateValue,
                        MyBundle.message("incorrectPropertyType")
                            .replace("{property}", key.text.unquoteAndCleanup())
                            .replace("{class}", targetClass.name),
                        ProblemHighlightType.WARNING,
                        TextRange(0, stateValue.textLength)
                    )
                }
            }
        }
    }
}