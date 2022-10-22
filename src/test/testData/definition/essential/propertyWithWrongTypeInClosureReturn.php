<?php

class Account {
    public function __construct(
        public readonly string $id,
        public readonly string $firstName,
        public readonly string $middleName,
        public readonly string $lastName,
        public readonly int $age,
    ) {}
}

class AccountFactory extends ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => 'abc',
            'firstName' => function (array $attributes) {
                return $attributes['id'];
            },
            'middleName' => fn (array $attributes) => $attributes['firstName'],
            'lastName' => function (array $attributes) {
                return $attributes['firstName'];
            },
            'age<caret>' => function (array $attributes) {
                return <warning descr="Incorrect type for property 'age' of 'Account' class">$attributes['id']</warning>;
            },
        ];
    }
}