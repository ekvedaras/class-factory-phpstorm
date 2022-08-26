package com.github.ekvedaras.classfactoryphpstorm

import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass

class Utilities private constructor() {
    companion object {
        fun String.unquoteAndCleanup() = this.replace("IntellijIdeaRulezzz", "").trim('\'', '"').trim()
        fun PhpClass.isClassFactory() = (this.extendsList.lastChild as ClassReference).name == "ClassFactory"
        fun Method.isClassFactoryDefinition() = this.name == "definition" && this.containingClass?.isClassFactory() ?: false
    }
}