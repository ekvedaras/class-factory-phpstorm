<?php

class Account {
    public function __construct(
        public readonly string <caret>$id,
        public readonly int $age,
    ) {}
}

class AccountFactory extends ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => 'abc',
            '<error desc="Property ages not found on class Account">ages</error>' => 1,
        ];
    }
}