<?php

class Id {
    public readonly string $value;
}

class Age {
    public readonly int $value;
}


class Account {
    public function __construct(
        public readonly Id <caret>$id,
        public readonly Age $age,
    ) {}
}

class IdFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Id::class;

    protected function definition(): array
    {
        return [
            'value' => 'abc',
        ];
    }
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
            'id' => IdFactory::new(),
            'age' => AgeFactory::new(),
        ];
    }

    public function specialState1(): static
    {
        return $this->state(fn (array $attributes) => [
            'id' => new Id(),
            'age<caret>' => <warning descr="Incorrect type for property 'age' of 'Account' class">$attributes['id']</warning>,
        ]);
    }

    public function specialState2(): static
    {
        return $this->state(fn (array $attributes) => [
            'id' => IdFactory::new(),
            'age' => $attributes['age'],
        ]);
    }
}