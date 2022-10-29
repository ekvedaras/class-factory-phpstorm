package com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.type.ClassFactoryPropertyDefinitionTypeProvider.Companion.getClassFactoryDefinitionType
import com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.type.AttributesArrayValueTypeProvider.Companion.getClassFactoryStateType
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.getClass
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
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class IncorrectPropertyTypeInspectionInInDirectlyPassedClosureReturnedArray : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpStringLiteralExpression(expression: StringLiteralExpression?) {
                if (expression == null) return

                if (DumbService.isDumb(expression.project)) return

                val arrayReturnedByClosure = expression.parent.parent.parent.parent.parent
                val arrayHashElement = expression.parent.parent.parent.parent

                if (arrayHashElement !is ArrayHashElement) return
                if (arrayReturnedByClosure !is ArrayCreationExpression) return

                val function = arrayReturnedByClosure.parentOfType<Function>() ?: return

                if (function.parent.parent.parent !is MethodReference) return
                val methodReference = function.parent.parent.parent as MethodReference

                val key = arrayHashElement.key ?: return

                val classFactoryMethodReference: ClassFactoryMethodReference = try {
                    when (true) {
                        methodReference.isClassFactoryState() -> StateMethodReferenceInsideFactory(methodReference)
                        methodReference.isClassFactoryMakeMethod() -> MakeMethodReference(methodReference)
                        methodReference.isClassFactoryStateMethod() -> StateMethodReferenceOutsideFactory(methodReference)
                        else -> return
                    }
                } catch (e: DomainException) {
                    return
                }

                val targetClass = classFactoryMethodReference.classFactory.targetClass
                val property = targetClass.getPropertyByName(key.text.unquoteAndCleanup()) ?: return

                val stateValue = arrayHashElement.value ?: return
                if (stateValue !is PhpTypedElement) return

                val stateValueType = stateValue.getClassFactoryStateType() ?: stateValue.type

                val factoryDefinitionValue =
                    classFactoryMethodReference.classFactory.definitionMethod.getPropertyDefinition(property.name)?.value
                        ?: property.type
                if (factoryDefinitionValue !is PhpTypedElement) return
                val factoryDefinitionValueType = factoryDefinitionValue.getClassFactoryDefinitionType() ?: factoryDefinitionValue.type

                val classFactoryUsedInState = stateValueType.isClassFactory(expression.project)
                val classFactoryUsedInDefinition = factoryDefinitionValueType.isClassFactory(expression.project)

                if (classFactoryUsedInState || classFactoryUsedInDefinition) {
                    if (
                        ((classFactoryUsedInState && ClassFactory(stateValueType.types.first().substringAfter("#C").substringBefore('.').getClass(expression.project) ?: return).targetClass.type != property.type))
                        || ((classFactoryUsedInDefinition && ClassFactory(factoryDefinitionValueType.types.first().substringAfter("#C").substringBefore('.').getClass(expression.project) ?: return).targetClass.type != property.type))
                    ) {
                        holder.registerProblem(
                            arrayHashElement.value ?: return,
                            MyBundle.message("incorrectPropertyType")
                                .replace("{property}", key.text.unquoteAndCleanup())
                                .replace("{class}", targetClass.name),
                            ProblemHighlightType.WARNING,
                            TextRange(0, arrayHashElement.value?.textLength ?: return)
                        )
                    }
                } else if (stateValueType != factoryDefinitionValueType) {
                    holder.registerProblem(
                        arrayHashElement.value ?: return,
                        MyBundle.message("incorrectPropertyType")
                            .replace("{property}", key.text.unquoteAndCleanup())
                            .replace("{class}", targetClass.name),
                        ProblemHighlightType.WARNING,
                        TextRange(0, arrayHashElement.value?.textLength ?: return)
                    )
                }
            }
        }
    }
}