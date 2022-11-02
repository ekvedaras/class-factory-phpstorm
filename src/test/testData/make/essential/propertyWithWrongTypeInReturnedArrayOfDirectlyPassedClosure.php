<?php

class Id {
    public string $value;
}

class Account {
    public function __construct(
        public readonly Id $id,
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
}

AccountFactory::new()->make(fn (array $attributes) => [
    'id' => new Id(),
    'age<caret>' => <warning descr="Incorrect type for property 'age' of 'Account' class">$attributes['id']</warning>,
]);

AccountFactory::new()->make(fn (array $attributes) => [
    'id' => new Id(),
    'age' => $attributes['age'],
]);