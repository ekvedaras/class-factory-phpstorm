package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.isClassFactoryDefinition
import com.github.ekvedaras.classfactoryphpstorm.support.Utilities.Companion.unquoteAndCleanup
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpReturn

class DefinitionMethod(val element: Method, ofClassFactory: ClassFactory? = null) : ClassFactoryMethodReference {
    override val classFactory: ClassFactory

    init {
        if (!element.isClassFactoryDefinition()) throw DefinitionMethodException.notClassFactoryDefinition()

        classFactory = ofClassFactory ?: ClassFactory(
            element.containingClass ?: throw DefinitionMethodException.noContainingClassFound()
        )
    }

    override val definedProperties: List<ArrayHashElement>
        get() {
            var properties = arrayOf<ArrayHashElement>()

            element.childrenOfType<GroupStatement>().forEach { groupStatement ->
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

    fun getPropertyDefinition(name: String): ClassFactoryPropertyDefinition? {
        return ClassFactoryPropertyDefinition(definedProperties.firstOrNull { it.key?.text?.unquoteAndCleanup() == name }
            ?: return null)
    }
}

internal class DefinitionMethodException(message: String) : DomainException(message) {
    companion object {
        fun notClassFactoryDefinition() =
            DefinitionMethodException("Given PSI method is not a ClassFactory definition method")

        fun noContainingClassFound() = DefinitionMethodException("Failed to load class of definition method")
    }
}