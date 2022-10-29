<?php

namespace EKvedaras\ClassFactory;

use Closure;

/**
 * @template T
 */
abstract class ClassFactory
{
    /** @var class-string<T> */
    protected string $class;

    /** @return static<T> */
    public static function new(): static
    {
    }

    abstract protected function definition(): array;

    /** @return static<T> */
    public function state(array | callable $state): static
    {
    }

    /** @return static<T> */
    public function after(Closure $transformer): static
    {
    }

    /** @return T */
    public function make(array | Closure $state = null): object
    {
    }
}
