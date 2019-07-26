package com.example.test

public abstract class UseCase<Q: UseCase.RequestValues, P: UseCase.ResponseValue> {
    public abstract fun executeUseCase(requestValues: Q): P

    public interface RequestValues {}
    public interface ResponseValue {}
}