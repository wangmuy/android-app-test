package com.example.test.empty;

import com.example.test.BasePresenter;
import com.example.test.BaseView;

public interface MyContract {

    interface Presenter extends BasePresenter {
        ;
    }

    interface View extends BaseView<Presenter> {
        ;
    }
}
