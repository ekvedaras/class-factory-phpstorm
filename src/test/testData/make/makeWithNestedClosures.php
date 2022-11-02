<?php

class Age {
    public function __construct(
        public readonly int <caret>$value,
    ) {}
}

class Person {
    public function __construct(
        public readonly string $name,
        public readonly Age $age,
    ) {}
}

class Account {
    public function __construct(
        public readonly string <caret>$id,
        public readonly Age $age,
        public readonly Person $person,
    ) {}
}

class AgeFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Age::class;

    protected function definition(): array
    {
        return [
            'value' => 1,
        ];
    }
}

class PersonFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Person::class;

    protected function definition(): array
    {
        return [
            'name' => 'John',
            'age' => AgeFactory::new(),
        ];
    }
}

class AccountFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => 'abc',
            'age' => AgeFactory::new(),
            'person' => PersonFactory::new(),
        ];
    }
}

AccountFactory::new()->make([
    'person' => fn (array $attributes) => $attributes['person']->make([
        'age' => fn (array $attributes) => $attributes['value'] * 2,
    ]),
]);