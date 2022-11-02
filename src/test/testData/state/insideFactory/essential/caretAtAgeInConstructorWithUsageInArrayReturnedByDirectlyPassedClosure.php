<?php

class Account {
    public function __construct(
        public readonly string $id,
        public readonly int <caret>$age,
    ) {}
}

class AccountFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => 'abc',
            'age' => 2,
        ];
    }

    public function specialState(): static
    {
        return $this->state(fn (array $attributes) => ['age' => $attributes['id']]);
    }
}