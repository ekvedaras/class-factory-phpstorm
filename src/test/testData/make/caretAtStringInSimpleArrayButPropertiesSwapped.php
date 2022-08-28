<?php

class Account {
    public function __construct(
        public readonly int $age,
        public readonly string $id,
    ) {}
}

class AccountFactory extends ClassFactory {
    protected string $class = Account::class;
}

AccountFactory::new()->make(['<caret>'])