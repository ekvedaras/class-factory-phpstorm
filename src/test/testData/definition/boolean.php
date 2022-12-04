<?php

class Account {
    public function __construct(
        public readonly string $id,
        public readonly bool $age,
    ) {}
}

class AccountFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => '1',
            'age' => false,
        ];
    }
}