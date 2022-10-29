<?php

class Account {
    public readonly string $id;
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

AccountFactory::new()->make(['<caret>' => 'abc'])