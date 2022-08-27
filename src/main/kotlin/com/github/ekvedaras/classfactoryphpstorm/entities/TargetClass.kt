package com.github.ekvedaras.classfactoryphpstorm.entities

import com.github.ekvedaras.classfactoryphpstorm.entities.TargetClassConstructor
import com.intellij.openapi.project.Project
import com.jetbrains.php.lang.psi.elements.PhpClass

class TargetClass(private val clazz: PhpClass) {
    val constructor : TargetClassConstructor?
        get() {
            return TargetClassConstructor(clazz.constructor ?: return null, this)
        }

    val name = clazz.name
}