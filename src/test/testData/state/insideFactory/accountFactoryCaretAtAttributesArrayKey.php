<?php

class RatingInputs {
    public static function default(string $currency): self
    {
    }
}

class Account
{
    public function __construct(
        public readonly string $id,
        public readonly bool $exists,
        public readonly string $accountReference,
        public readonly string $name,
        public readonly ?string $tradingName,
        public readonly string $currency,
        public RatingInputs $ratingInputs,
    ) {
    }
}

/** @extends EKvedaras\ClassFactory\ClassFactory<Account> */
class AccountFactory extends EKvedaras\ClassFactory\ClassFactory
{
    protected string $class = Account::class;

    protected function definition(): array
    {
        return [
            'id' => 'b3fb37f8-c6d4-498c-8e98-9a8919587f53',
            'exists' => false,
            'accountReference' => 'ABC123',
            'name' => 'John Doe',
            'tradingName' => 'John Doe Inc.',
            'currency' => 'GBP',
            'ratingInputs' => new RatingInputs(),
        ];
    }

    public function withDefaultRatingInputs(): static
    {
        return $this->state(fn (array $attributes) => [
            'ratingInputs' => RatingInputs::default($attributes['<caret>']),
        ]);
    }
}
