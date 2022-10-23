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

class IdFactory extends ClassFactory {
    protected string $class = Id::class;

    protected function definition(): array
    {
        return [
            'value' => 'abc',
        ];
    }
}

class AgeFactory extends ClassFactory {
    protected string $class = Age::class;

    protected function definition(): array
    {
        return [
            'value' => 1,
        ];
    }
}

class AccountFactory extends ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => IdFactory::new(),
            'age' => AgeFactory::new(),
        ];
    }
}

AccountFactory::new()->state([
    'id' => IdFactory::new(),
    'age' => <warning descr="Incorrect type for property 'age' of 'Account' class">IdFactory::new()</warning>,
]);