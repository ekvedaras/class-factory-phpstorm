<?php

class Account {
    public readonly string $id;
    public readonly int $age;
}

class AccountFactory extends ClassFactory {
    protected string $class = Account::class;
}

AccountFactory::new()->make(['<caret>' => 'abc'])