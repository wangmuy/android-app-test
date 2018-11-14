package com.example.test.network;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

public abstract class Utils {
    private static final int TOTAL_ATTEMPT = 3;

    public static <T> ObservableTransformer<T, T> applyCommon() {
        return upstream -> upstream
            .retryWhen(throwableObs ->  // exponential back off
                throwableObs.zipWith(Observable.range(1, TOTAL_ATTEMPT), (throwable, attemptCount) ->
                    attemptCount < TOTAL_ATTEMPT?
                        Observable.timer(10 * (1 << (attemptCount-1)), TimeUnit.SECONDS):
                        Observable.error(throwable))
                    .flatMap(x -> x)
            );
    }
}
