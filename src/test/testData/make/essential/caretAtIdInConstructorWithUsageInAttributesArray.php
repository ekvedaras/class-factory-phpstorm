<?php

class Account {
    public function __construct(
        public readonly string <caret>$id,
        public readonly int $age,
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
}

AccountFactory::new()->make(['age' => function (array $attributes) {
    return (int) $attributes['id'];
}]);