package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.github.ekvedaras.classfactoryphpstorm.support.DomainException
import com.jetbrains.php.lang.psi.elements.Method

class TargetClassConstructor(private val constructor: Method, private val targetClass: TargetClass) {
    init {
        if (constructor.name != "__construct") throw TargetClassConstructorException.notConstructor(constructor)
    }

    val parameters: List<TargetClassConstructorParameter>
        get() = constructor.parameters.map { TargetClassConstructorParameter(it, targetClass) }

    val totalParameters: Int
        get() = this.parameters.size

    fun getParameterByName(name: String): TargetClassConstructorParameter? {
        return TargetClassConstructorParameter(constructor.parameters.firstOrNull { it.name == name } ?: return null,
            targetClass)
    }

    fun getParameterIndex(parameter: TargetClassConstructorParameter) = this.parameters.indexOfFirst {
        it.parameter.name == parameter.parameter.name
    }
}

internal class TargetClassConstructorException(message: String) : DomainException(message) {
    companion object {
        fun notConstructor(given: Method) =
            TargetClassConstructorException("Given PSI method name must be __construct. ${given.name} given.")
    }
}