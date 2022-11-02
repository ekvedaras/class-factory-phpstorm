<?php

class Name {
    public string $value;
}

class Account {
    public function __construct(
        public readonly string $id,
        public readonly Name $nickName,
        public readonly Name $firstName,
        public readonly Name $middleName,
        public readonly Name $lastName,
        public readonly int $age,
    ) {}
}

class AccountFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => 'abc',
            'nickName' => new Name(),
            'firstName' => function (array $attributes) {
                return $attributes['nickName'];
            },
            'middleName' => fn (array $attributes) => $attributes['firstName'],
            'lastName' => fn (array $attributes) => $attributes['middleName'],
            'age' => fn (array $attributes) => (int) $attributes['id'],
        ];
    }
}

AccountFactory::new()->state([
    'lastName' => fn (array $attributes) => $attributes['middleName']-><caret>,
]);