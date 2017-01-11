package com.example.test;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnFocusChange;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.recyclerview) RecyclerView mRecyclerView;
    SimpleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        mRecyclerView.setChildDrawingOrderCallback(new RecyclerView.ChildDrawingOrderCallback() {
//            private int focused = Integer.MAX_VALUE;
//            @Override
//            public int onGetChildDrawingOrder(int childCount, int i) {
//                final boolean isFocused = mRecyclerView.getChildAt(i).isFocused();
//                int order = i;
//                final boolean isLast = (i == childCount -1);
//                if(isFocused) {
//                    order = childCount-1;
//                    focused = i;
//                }
//                if(isLast && !isFocused) {
//                    order = focused;
//                }
//                return order;
//            }
//        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new SimpleAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        setModel();
    }

    private void setModel() {
        mAdapter.setModel(new String[]{"s1", "s2", "s3"});
        mAdapter.notifyDataSetChanged();
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                View v = mRecyclerView.getChildAt(0);
                Log.d(TAG, "setModel requestFocus on "+v);
                if(v != null)
                    v.requestFocus();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    static class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {
        private LayoutInflater lf;
        private String[] model;

        public SimpleAdapter(Context c) {
            lf = LayoutInflater.from(c);
        }

        public void setModel(Object[] m) {
            model = (String[]) m;
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = lf.inflate(R.layout.list_item, parent, false);
            return new SimpleViewHolder(v);
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
            if(model == null || position >= model.length)
                return;
            String s = model[position];
            if(s != null) {
                holder.mText.setText(s);
            }
            holder.position = position;
            Log.d(TAG, "onBindViewHolder: "+position+"/"+model.length+"/"+s);
        }

        @Override
        public int getItemCount() {
            int count = model != null? model.length : 0;
            return count;
        }

        public static class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener {
            private int position;
            @BindView(R.id.item_image) ImageView mThumbnail;
            @BindView(R.id.item_text) TextView mText;

            public SimpleViewHolder(View itemView) {
                super(itemView);
                itemView.setTag(this);
                ButterKnife.bind(this, itemView);
            }

            @OnFocusChange(R.id.item_frame)
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if(!view.isAttachedToWindow())
                    return;
                if(isFocused)
                    view.bringToFront();
                final float scaleTo = (isFocused? 1.1f : 1.0f);
                view.animate().scaleX(scaleTo).scaleY(scaleTo).setDuration(200).start();
                final float TRANSY = 100f;
                final float textTranslationY = (isFocused? 0 : TRANSY);
                final float textAlphaTo = (isFocused? 1.0f : 0f);
                SimpleViewHolder holder = (SimpleViewHolder) view.getTag();
                if(isFocused) {
                    ObjectAnimator animTransY = ObjectAnimator.ofFloat(holder.mText, "textTranslationY", TRANSY, textTranslationY);
                    ObjectAnimator animAlpha = ObjectAnimator.ofFloat(holder.mText, "alpha", textAlphaTo);
                    AnimatorSet as = new AnimatorSet().setDuration(200);
                    as.playTogether(animTransY, animAlpha);
                    as.start();
                } else {
                    holder.mText.setTranslationY(textTranslationY);
                    holder.mText.setAlpha(textAlphaTo);
                }
            }
        }
    }

}
