<?php

class Age {
    public function __construct(
        public readonly int $value,
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

class AccountFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => 'abc',
            'age' => AgeFactory::new(),
            'person' => new Person('John', 1),
        ];
    }

    public function old(): static
    {
        return $this->state([
            'age' => fn (array $attributes) => $attributes['age']->state(['value' => 100]),
        ]);
    }
}

AccountFactory::new()->old()->make(['person' => fn (array $attributes) => new Person(
    name: 'John',
    age: $attributes['age']->make(),
)]);