package com.github.ekvedaras.classfactoryphpstorm.entities

import com.jetbrains.php.lang.psi.elements.Method

class TargetClassConstructor(private val constructor: Method, private val targetClass: TargetClass) {
    init {
        if (constructor.name != "__construct") throw Exception("Given PSI method name must be __construct. ${constructor.name} given.")
    }

    val parameters : List<TargetClassConstructorParameter>
        get() = constructor.parameters.map { TargetClassConstructorParameter(it, targetClass) }

    fun getPropertyByName(name: String) : TargetClassConstructorParameter? {
        return TargetClassConstructorParameter(constructor.parameters.firstOrNull { it.name == name } ?: return null, targetClass)
    }
}