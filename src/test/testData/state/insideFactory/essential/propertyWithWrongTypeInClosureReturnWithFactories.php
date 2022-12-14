<?php

class Id {
    public string $value;
}

class Name {
    public string $value;
}

class Account {
    public function __construct(
        public readonly Id $id,
        public readonly Name $nickName,
        public readonly Name $firstName,
        public readonly Name $middleName,
        public readonly Name $lastName,
        public readonly int $age,
    ) {}
}

class IdFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Id::class;

    protected function definition(): array
    {
        return ['value' => 'abc'];
    }
}

class NameFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Name::class;

    protected function definition(): array
    {
        return ['value' => 'John'];
    }

    public function specialState(): static
    {
        return $this->state(['value' => 'Smith']);
    }
}

class AccountFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => IdFactory::new(),
            'nickName' => NameFactory::new()->make(),
            'firstName' => function (array $attributes) {
                return $attributes['nickName'];
            },
            'middleName' => fn (array $attributes) => $attributes['firstName'],
            'lastName' => function (array $attributes) {
                return $attributes['firstName'];
            },
            'age' => 1,
        ];
    }

    public function specialState1(): static
    {
        return $this->state(['lastName' => function (array $attributes) {
            return <warning descr="Incorrect type for property 'lastName' of 'Account' class">$attributes['id']</warning>;
        }]);
    }

    public function specialState2(): static
    {
        return $this->state(['lastName' => function (array $attributes) {
            return $attributes['lastName']->specialState();
        }]);
    }

    public function specialState3(): static
    {
        return $this->state(['lastName<caret>' => function (array $attributes) {
            return $attributes['lastName'];
        }]);
    }
}