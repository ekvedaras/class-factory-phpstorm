<?php

class Account {
    public readonly string <caret>$id;
    public readonly int $age;
}

class AccountFactory extends ClassFactory {
    protected string $class = Account::class;

    public function specialState(): static
    {
        return $this->state(['id' => 1]);
    }
}