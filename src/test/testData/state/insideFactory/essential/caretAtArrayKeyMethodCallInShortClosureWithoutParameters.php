<?php

class Id {
    public string $value;
}

class Account {
    public function __construct(
        public readonly string $id,
        public readonly int $age,
    ) {}
}

class AccountFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => new Id(),
            'age' => 1,
        ];
    }

    public function specialState(): static
    {
        $attributes = [];

        return $this->state(['age' => fn () => $attributes['id']-><caret>]);
    }
}