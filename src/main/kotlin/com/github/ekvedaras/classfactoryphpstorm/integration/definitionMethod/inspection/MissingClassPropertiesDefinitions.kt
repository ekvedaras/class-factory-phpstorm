package com.github.ekvedaras.classfactoryphpstorm.integration.definitionMethod.inspection

import com.github.ekvedaras.classfactoryphpstorm.MyBundle
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.github.ekvedaras.classfactoryphpstorm.support.entities.DefinitionMethod
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

class MissingClassPropertiesDefinitions : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpArrayCreationExpression(expression: ArrayCreationExpression?) {
                if (expression == null) return

                if (DumbService.isDumb(expression.project)) return

                if (expression.parent !is PhpReturn) return

                val method = expression.parentOfType<Method>() ?: return
                if (!method.isClassFactoryDefinition()) return

                val definitionMethod = DefinitionMethod(method)
                val targetClass = definitionMethod.classFactory.targetClass ?: return

                val alreadyDefinedProperties = definitionMethod.definedProperties
                val missingProperties = targetClass.properties
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
                        .replace("{class}", targetClass.name),
                    ProblemHighlightType.WARNING,
                )
            }
        }
    }
}