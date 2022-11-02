<?php

class Account {
    public function __construct(
        public readonly string $id,
        public readonly int $age,
    ) {}
}

class AccountFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => 'abc',
            'age' => 1,
        ];
    }
}

AccountFactory::new()->make(['id<caret>' => function (array $attributes) {
    return $attributes['<warning descr="Cannot resolve argument 'ages' of 'Account' class constructor">ages</warning>'];
}]);