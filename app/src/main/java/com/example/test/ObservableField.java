package com.example.test;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class ObservableField<T> {
    // retain most recently emitted
    private BehaviorSubject<T> subject;

    public ObservableField(T value) {
        subject = BehaviorSubject.create(value);
    }

    public ObservableField<T> set(T newVal) {
        subject.onNext(newVal);
        return this;
    }

    public T get() {
        return subject.getValue();
    }

    public Observable<T> asObservable() {
        return subject.asObservable();
    }
}
