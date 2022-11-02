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

    public function specialState(): array
    {
        return $this->state([
            'id' => 'abc',
            'age' => 2,
            '<caret>'
        ]);
    }
}