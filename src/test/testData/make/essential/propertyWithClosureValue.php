<?php

class Account {
    public function __construct(
        public readonly string <caret>$id,
        public readonly \Closure $age,
    ) {}
}

class AccountFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => 'abc',
            'age' => EKvedaras\ClassFactory\ClosureValue::of(fn () => 1),
        ];
    }
}

AccountFactory::new()->make(['age' => EKvedaras\ClassFactory\ClosureValue::of(fn () => 2)]);