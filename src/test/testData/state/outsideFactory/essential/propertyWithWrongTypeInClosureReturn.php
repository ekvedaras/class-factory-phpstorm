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

AccountFactory::new()->state(['age' => function (array $attributes) {
    return <warning descr="Incorrect type for property 'age' of 'Account' class">$attributes['id']</warning>;
}]);

AccountFactory::new()->state(['age' => function (array $attributes) {
    return (int) $attributes['id'];
}]);

AccountFactory::new()->state(['age<caret>' => function (array $attributes) {
    return $attributes['age'];
}]);