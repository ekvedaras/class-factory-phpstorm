<?php

class Id {
    public string $value;
}

class Name {
    public string $value;
}

class Account {
    public function __construct(
        public readonly Id $id,
        public readonly Name $nickName,
        public readonly Name $firstName,
        public readonly Name $middleName,
        public readonly Name $lastName,
        public readonly int $age,
    ) {}
}

class IdFactory extends ClassFactory {
    protected string $class = Id::class;

    protected function definition(): array
    {
        return ['value' => 'abc'];
    }
}

class NameFactory extends ClassFactory {
    protected string $class = Name::class;

    protected function definition(): array
    {
        return ['value' => 'John'];
    }
}

class AccountFactory extends ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => IdFactory::new(),
            'nickName' => NameFactory::new()->make(),
            'firstName' => function (array $attributes) {
                return $attributes['nickName'];
            },
            'middleName' => fn (array $attributes) => $attributes['firstName'],
            'lastName' => function (array $attributes) {
                return $attributes['firstName'];
            },
            'age' => 1,
        ];
    }
}

AccountFactory::new()->make(['lastName' => function (array $attributes) {
    return <warning descr="Incorrect type for property 'lastName' of 'Account' class">$attributes['id']</warning>;
}]);

AccountFactory::new()->make(['lastName' => function (array $attributes) {
    return $attributes['lastName']->make();
}]);

AccountFactory::new()->make(['lastName<caret>' => function (array $attributes) {
    return $attributes['lastName'];
}]);