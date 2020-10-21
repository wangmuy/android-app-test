package com.example.test.empty;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.test.Injection;
import com.example.test.R;

public class MyMainActivity extends AppCompatActivity {

    private MyPresenter mMyPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity_main);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MyFragment frag = (MyFragment) supportFragmentManager.findFragmentById(R.id.my_contentFrame);
        if (frag == null) {
            frag = MyFragment.newInstance();
            FragmentTransaction transact = supportFragmentManager.beginTransaction();
            transact.replace(R.id.my_contentFrame, frag);
            transact.commit();
        }

        mMyPresenter = new MyPresenter(frag, Injection.INSTANCE.providerSchedulerProvider());
    }
}
