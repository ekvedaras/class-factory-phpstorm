package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.jetbrains.php.lang.psi.elements.PhpClass

class TargetClass(private val clazz: PhpClass) {
    val constructor: TargetClassConstructor?
        get() {
            return TargetClassConstructor(clazz.constructor ?: return null, this)
        }

    val fields: List<TargetClassField>
        get() {
            return clazz.fields.map { TargetClassField(it, this) }
        }

    val properties: List<TargetClassParameter>
        get() {
            return this.constructor?.parameters ?: this.fields
        }

    fun getPropertyByName(name: String): TargetClassParameter?
        = this.properties.firstOrNull { it.name == name }

    val name = clazz.name
}