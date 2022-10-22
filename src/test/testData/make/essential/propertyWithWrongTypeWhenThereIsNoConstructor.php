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

AccountFactory::new()->make([
    'id' => 'abd',
    'age' => <warning descr="Incorrect type for property 'age' of 'Account' class">'1'</warning>,
]);