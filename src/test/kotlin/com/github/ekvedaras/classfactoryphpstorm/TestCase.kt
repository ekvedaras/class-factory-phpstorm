package com.github.ekvedaras.classfactoryphpstorm

import com.intellij.codeInspection.InspectionProfileEntry
import com.intellij.testFramework.TestDataFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase

internal abstract class TestCase : BasePlatformTestCase() {
    override fun setUp() {
        super.setUp()

        myFixture.configureByText("classFactory.php", """<?php

namespace EKvedaras\ClassFactory;

use Closure;

/**
 * @template T
 */
abstract class ClassFactory
{
    /** @var class-string<T> */
    protected string ${'$'}class;

    /** @return static<T> */
    public static function new(): static
    {
    }

    abstract protected function definition(): array;

    /** @return static<T> */
    public function state(array | callable ${'$'}state): static
    {
    }

    /** @return static<T> */
    public function after(Closure ${'$'}transformer): static
    {
    }

    /** @return T */
    public function make(array | Closure ${'$'}state = null): object
    {
    }
}

class ClosureValue
{
    public function __construct(public readonly Closure ${'$'}value)
    {
    }

    public static function of(Closure ${'$'}value): self
    {
    }
}
""")
    }

    protected fun assertCompletionContains(vararg shouldContain: String) {
        val strings = myFixture.lookupElementStrings ?: return fail("Empty completion result")

        assertContainsElements(strings, shouldContain.asList())
    }

    protected fun assertCompletionDoesNotContain(vararg shouldNotContain: String) {
        val strings = myFixture.lookupElementStrings ?: return

        assertDoesntContain(strings, shouldNotContain.asList())
    }

    protected fun assertInspection(
        @TestDataFile filePath: String,
        inspection: InspectionProfileEntry,
    ) {
        myFixture.enableInspections(inspection)
        myFixture.testHighlighting(filePath)
    }
}
