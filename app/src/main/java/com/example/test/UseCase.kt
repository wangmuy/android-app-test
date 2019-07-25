package com.example.test

public abstract class UseCase<Q: UseCase.RequestValues, P: UseCase.ResponseValue> {
    private lateinit var requestValues: Q
    private lateinit var useCaseCallback: UseCaseCallback<P>

    public fun getRequestValues(): Q {
        return requestValues
    }

    public fun setRequestValues(requestValues: Q) {
        this.requestValues = requestValues
    }

    public fun getUseCaseCallback(): UseCaseCallback<P> {
        return useCaseCallback
    }

    public fun setUseCaseCallback(useCaseCallback: UseCaseCallback<P>) {
        this.useCaseCallback = useCaseCallback
    }

    public abstract fun executeUseCase(requestValues: Q)

    public interface RequestValues {}
    public interface ResponseValue {}

    public interface UseCaseCallback<R> {
        fun onSuccess(response: R)
        fun onError()
    }
}