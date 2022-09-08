<?php

class Account {
    public function __construct(
        public readonly string $id,
        public readonly int $age,
    ) {}
}

class AccountFactory extends ClassFactory {
    protected string $class = Account::class;
}

AccountFactory::new()->make([
                                 'id' => 'abc',
                                 'age' => 2,
                                 '<caret>'
                             ])