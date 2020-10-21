package com.example.test.empty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.test.R;

public class MyFragment extends Fragment implements MyContract.View {

    private MyContract.Presenter mPresenter;

    private TextView mTextView;

    @Override
    public MyContract.Presenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void setPresenter(MyContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.my_frag, container, false);
        mTextView = root.findViewById(R.id.my_frag_textView1);
        return root;
    }

    public static MyFragment newInstance() {
        return new MyFragment();
    }
}
