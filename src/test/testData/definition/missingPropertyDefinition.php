<?php

class Account {
    public function __construct(
        public readonly string $id,
        public readonly int $age,
        public readonly int $createdAt,
        public readonly bool $isAdmin = false,
    ) {}
}

class AccountFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return <warning descr="Missing definition for 'age', 'createdAt' of 'Account' constructor">[
            'id' => 'abc',
        ]</warning>;
    }
}