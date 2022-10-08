<?php

class Account {
    public readonly string $id;
    public readonly int $age;
}

class AccountFactory extends ClassFactory {
    protected string $class = Account::class;

    public function specialState(): array
    {
        return $this->state([
            '<caret>' => 'abc',
        ]);
    }
}