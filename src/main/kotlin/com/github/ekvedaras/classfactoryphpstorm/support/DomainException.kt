package com.github.ekvedaras.classfactoryphpstorm.support

abstract class DomainException(override val message: String) : Exception(message)