<?php

class Age {
    public function __construct(
        public readonly int $value,
    ) {}

    public static function factory(): AgeFactory
    {
        return AgeFactory::new();
    }
}

class AgeFactory extends EKvedaras\ClassFactory\ClassFactory {
    protected string $class = Age::class;

    protected function definition(): array
    {
        return [
            'value' => 1,
        ];
    }
}

Age::factory()->make(['<caret>']);