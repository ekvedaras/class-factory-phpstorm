package com.github.ekvedaras.classfactoryphpstorm.entities

import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.Utilities.Companion.unquoteAndCleanup
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpReturn

class DefinitionMethod(private val method: Method) {
    val classFactory: ClassFactory

    init {
        if (!method.isClassFactoryDefinition()) throw Exception("Given PSI method is not a ClassFactory definition method.")
        classFactory = ClassFactory(
            method.containingClass ?: throw Exception("Failed to load ClassFactory from definition method")
        )
    }

    val definedProperties: List<ArrayHashElement>
        get() {
            var properties = arrayOf<ArrayHashElement>()

            method.childrenOfType<GroupStatement>().forEach { groupStatement ->
                groupStatement.childrenOfType<PhpReturn>().forEach { definitionReturn: PhpReturn ->
                    definitionReturn.childrenOfType<ArrayCreationExpression>()
                        .forEach { definition: ArrayCreationExpression ->
                            definition.childrenOfType<ArrayHashElement>()
                                .forEach { propertyDefinition: ArrayHashElement ->
                                    properties += propertyDefinition
                                }
                        }
                }
            }

            return properties.toList()
        }

    fun getPropertyDefinition(name: String) =
        definedProperties.firstOrNull { it.key?.text?.unquoteAndCleanup() == name }
}