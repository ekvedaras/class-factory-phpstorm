package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.domain.method.definition.DefinitionMethod
import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class MissingClassPropertiesDefinitions : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpArrayCreationExpression(expression: ArrayCreationExpression) {
                if (DumbService.isDumb(expression.project)) return
                if (expression.parent !is PhpReturn) return

                val definitionMethod = try {
                    DefinitionMethod(expression.parentOfType() ?: return)
                } catch (e: DomainException) {
                    return
                }

                val alreadyDefinedProperties = definitionMethod.definedProperties
                val missingProperties = definitionMethod
                    .classFactory
                    .targetClass
                    .properties
                    .filterNot { it.isOptional }
                    .filterNot {
                        alreadyDefinedProperties.find { definedProperty ->
                            it.name == definedProperty.key?.text?.unquoteAndCleanup()
                        } != null
                    }

                if (missingProperties.isEmpty()) return

                holder.registerProblem(
                    expression,
                    MyBundle.message("missingClassPropertiesDefinitions")
                        .replace("{properties}", missingProperties.joinToString { "'${it.name}'" })
                        .replace("{class}", definitionMethod.classFactory.targetClass.name),
                    ProblemHighlightType.WARNING,
                )
            }
        }
    }
}