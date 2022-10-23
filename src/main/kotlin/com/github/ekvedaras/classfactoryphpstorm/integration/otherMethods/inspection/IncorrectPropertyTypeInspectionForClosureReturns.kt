package com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.type.ClassFactoryPropertyDefinitionTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.type.AttributesArrayValueTypeProvider
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.getClass
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactory
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryStateMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.support.entities.ClassFactory
import com.github.ekvedaras.classfactoryphpstorm.support.entities.ClassFactoryMethodReference
import com.github.ekvedaras.classfactoryphpstorm.support.entities.MakeMethodReference
import com.github.ekvedaras.classfactoryphpstorm.support.entities.StateMethodReferenceInsideFactory
import com.github.ekvedaras.classfactoryphpstorm.support.entities.StateMethodReferenceOutsideFactory
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

                val classFactoryMethodReference: ClassFactoryMethodReference = when (true) {
                    methodReference.isClassFactoryState() -> StateMethodReferenceInsideFactory(methodReference)
                    methodReference.isClassFactoryMakeMethod() -> MakeMethodReference(methodReference)
                    methodReference.isClassFactoryStateMethod() -> StateMethodReferenceOutsideFactory(methodReference)
                    else -> return
                }

                val targetClass = classFactoryMethodReference.classFactory.targetClass ?: return
                val property = targetClass.getPropertyByName(key.text.unquoteAndCleanup()) ?: return

                val stateValue = expression.firstPsiChild ?: return
                if (stateValue !is PhpTypedElement) return

                val stateValueType = if (stateValue is ArrayAccessExpression) {
                    AttributesArrayValueTypeProvider().getType(stateValue) ?: stateValue.type
                } else {
                    stateValue.type
                }

                val factoryDefinitionValue =
                    classFactoryMethodReference.classFactory.definitionMethod?.getPropertyDefinition(property.name)?.value
                        ?: property.type
                if (factoryDefinitionValue !is PhpTypedElement) return
                val factoryDefinitionValueType =
                    ClassFactoryPropertyDefinitionTypeProvider().getType(factoryDefinitionValue)
                        ?: factoryDefinitionValue.type

                // TODO There must be a better way
                val classFactoryUsed = !stateValueType.isAmbiguous && stateValueType.types.first().substringAfter("#C").substringBefore('.').getClass(expression.project)?.isClassFactory() == true

                if ((classFactoryUsed && ClassFactory(stateValueType.types.first().substringAfter("#C").substringBefore('.').getClass(expression.project) ?: return).targetClass?.type != property.type) || (!classFactoryUsed && stateValueType != factoryDefinitionValueType)) {
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