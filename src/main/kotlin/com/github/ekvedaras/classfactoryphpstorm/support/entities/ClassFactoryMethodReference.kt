package com.github.ekvedaras.classfactoryphpstorm.support.entities

import com.jetbrains.php.lang.psi.elements.ArrayHashElement

interface ClassFactoryMethodReference {
    val classFactory: ClassFactory
    val definedProperties: List<ArrayHashElement>
}