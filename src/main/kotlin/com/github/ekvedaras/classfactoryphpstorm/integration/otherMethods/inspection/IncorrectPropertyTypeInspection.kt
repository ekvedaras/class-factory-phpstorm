package com.github.ekvedaras.classfactoryphpstorm.integration.otherMethods.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.getClass
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isArrayHashValueOf
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactory
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryMakeMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryState
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryStateMethod
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.domain.ClassFactory
import com.github.ekvedaras.classfactoryphpstorm.domain.ClassFactoryMethodReference
import com.github.ekvedaras.classfactoryphpstorm.domain.method.make.MakeMethodReference
import com.github.ekvedaras.classfactoryphpstorm.domain.method.state.StateMethodReferenceInsideFactory
import com.github.ekvedaras.classfactoryphpstorm.domain.method.state.StateMethodReferenceOutsideFactory
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class IncorrectPropertyTypeInspection : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpStringLiteralExpression(expression: StringLiteralExpression?) {
                if (expression == null) return

                if (DumbService.isDumb(expression.project)) return

                val arrayHashElement = expression.parent.parent
                if (arrayHashElement !is ArrayHashElement) return
                if (expression.isArrayHashValueOf(arrayHashElement)) return
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
                val property = targetClass.getPropertyByName(expression.text.unquoteAndCleanup()) ?: return
                val factoryValue = arrayHashElement.value ?: return
                if (factoryValue !is PhpTypedElement) return

                // TODO There must be a better way
                val classFactoryUsed =
                    factoryValue is MethodReference && factoryValue.classReference is ClassReference && (factoryValue.classReference as ClassReference).getClass()
                        ?.isClassFactory() == true

                if ((classFactoryUsed && ClassFactory(
                        ((factoryValue as MethodReference).classReference as ClassReference).getClass() ?: return
                    ).targetClass.type != property.type) || (!classFactoryUsed && property.type != factoryValue.type)
                ) {
                    holder.registerProblem(
                        factoryValue,
                        MyBundle.message("incorrectPropertyType")
                            .replace("{property}", expression.text.unquoteAndCleanup())
                            .replace("{class}", targetClass.name),
                        ProblemHighlightType.WARNING,
                        TextRange(0, factoryValue.textLength)
                    )
                }
            }
        }
    }
}