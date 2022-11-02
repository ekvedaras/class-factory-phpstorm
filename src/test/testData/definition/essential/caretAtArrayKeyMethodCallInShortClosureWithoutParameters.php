<?php

class Id {
    public string $value;
}

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
        $attributes = [];

        return [
            'id' => new Id(),
            'age' => fn () => $attributes['id']-><caret>,
        ];
    }
}