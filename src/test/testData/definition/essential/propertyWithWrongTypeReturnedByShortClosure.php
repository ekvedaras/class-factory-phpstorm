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
            'age' => fn () => <warning descr="Incorrect type for property 'age' of 'Account' class">'1'</warning>,
        ];
    }
}