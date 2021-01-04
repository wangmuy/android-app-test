package com.example.test.empty;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.eclipsesource.v8.V8;
import com.example.test.R;

public class MyFragment extends Fragment implements MyContract.View {
    private static final String TAG = "MyFragment";

    private MyContract.Presenter mPresenter;

    private Button mBtn;
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
        mBtn = root.findViewById(R.id.my_test_btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTestBtnClicked();
            }
        });
        return root;
    }

    private void onTestBtnClicked() {
        try {
            testJ2v8();
        } catch (Exception e) {
            mTextView.setText(e.getMessage());
            Log.e(TAG, "", e);
        }
    }

    private void testJ2v8() {
        StringBuilder sb = new StringBuilder();
        ApplicationInfo appInfo = getContext().getApplicationInfo();
        sb.append("app lib path=").append(appInfo.nativeLibraryDir).append("\n");
        String script = "var hello = 'hello';\n"
                + "var world = 'world';\n"
                + "hello.concat(world).length;";

        try {
            V8 runtime = V8.createV8Runtime();
            int result = runtime.executeIntegerScript(script);
            sb.append("result=").append(result).append("\n");
            runtime.close();
        } catch (Throwable e) {
            sb.append(e.getMessage()).append("\n");
        }
        mTextView.setText(sb.toString());
    }

    public static MyFragment newInstance() {
        return new MyFragment();
    }
}
