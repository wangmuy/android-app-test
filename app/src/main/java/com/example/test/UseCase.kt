package com.example.test

abstract class UseCase<Q: UseCase.RequestValues, P: UseCase.ResponseValue> {
    abstract fun executeUseCase(requestValues: Q): P

    interface RequestValues {}
    interface ResponseValue {}
}