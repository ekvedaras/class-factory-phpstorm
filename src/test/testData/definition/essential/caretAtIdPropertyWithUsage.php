<?php

class Account {
    public readonly string <caret>$id;
    public readonly int $age;
}

class AccountFactory extends ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => 'abc',
            'age' => 1,
        ];
    }
}