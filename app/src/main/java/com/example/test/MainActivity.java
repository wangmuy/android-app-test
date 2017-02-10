package com.example.test;


import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import com.example.test.KeyframeAnimatorHelper.KeyframeSentinel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.textview)
    View mTextView;

    @BindView(R.id.startBtn)
    View mStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private Interpolator mInterpolator1st = new LinearOutSlowInInterpolator();
    private Interpolator mInterpolator2nd = new AccelerateDecelerateInterpolator();

    @OnClick(R.id.startBtn)
    public void startAnim(View btn) {
        final int ANIM_OFFSET = 200;
        Animator anim = KeyframeAnimatorHelper.ofKeyframe(mTextView, "translationX",
                KeyframeSentinel.ofFloatOrigin(),
                KeyframeSentinel.ofFloat(ANIM_OFFSET, 200, mInterpolator1st),
                KeyframeSentinel.ofFloat(0f, 170, mInterpolator2nd));
        anim.start();
    }
}
