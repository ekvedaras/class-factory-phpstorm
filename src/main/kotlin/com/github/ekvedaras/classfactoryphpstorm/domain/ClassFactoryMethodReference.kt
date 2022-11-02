package com.github.ekvedaras.classfactoryphpstorm.domain

import com.jetbrains.php.lang.psi.elements.ArrayHashElement

interface ClassFactoryMethodReference {
    val classFactory: ClassFactory
    val definedProperties: List<ArrayHashElement>
}