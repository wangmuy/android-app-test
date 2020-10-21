package com.example.test.empty;

import com.example.test.util.schedulers.BaseSchedulerProvider;

public class MyPresenter implements MyContract.Presenter {
    private MyContract.View mMyView;

    private BaseSchedulerProvider mSchedulerProvider;

    public MyPresenter(MyContract.View myView, BaseSchedulerProvider schedulerProvider) {
        mMyView = myView;
        mMyView.setPresenter(this);
        mSchedulerProvider = schedulerProvider;
    }

    @Override
    public void subscribe() {
        ;
    }

    @Override
    public void unsubscribe() {
        ;
    }
}
