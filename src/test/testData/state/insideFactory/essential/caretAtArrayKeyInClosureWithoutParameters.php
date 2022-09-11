<?php

class Account {
    public function __construct(
        public readonly string $id,
        public readonly int $age,
    ) {}
}

class AccountFactory extends ClassFactory {
    protected string $class = Account::class;

    public function specialState(): static
    {
        return $this->state(['id' => function () {
            $array = [];

            return $array['<caret>'];
        }]);
    }
}