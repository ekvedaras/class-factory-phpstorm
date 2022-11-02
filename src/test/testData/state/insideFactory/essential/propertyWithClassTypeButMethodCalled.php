<?php

class Age {
    public static function of(int $age): static
    {
    }
}

class Account {
    public function __construct(
        public readonly string <caret>$id,
        public readonly Age $age,
    ) {}
}

class AccountFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => 'abc',
            'age' => new Age(),
        ];
    }

    public function specialState(): static
    {
        return $this->state(['age' => Age::of(2)]);
    }
}