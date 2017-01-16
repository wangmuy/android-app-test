package com.example.test;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.recyclerview) RecyclerView mRecyclerView;
    SimpleAdapter mAdapter;
    @BindView(R.id.removeBtn) Button mCloseBtn;

    private SimpleItemAnimator mAnimator = new MyItemAnimator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        new Instrumentation().setInTouchMode(false);

        mRecyclerView.setItemAnimator(mAnimator);
        mRecyclerView.setChildDrawingOrderCallback(new RecyclerView.ChildDrawingOrderCallback() {
            private int focused = Integer.MAX_VALUE;
            @Override
            public int onGetChildDrawingOrder(int childCount, int i) {
                int order = i;
                final boolean isLast = (i == childCount -1);
                final boolean isFocused = mRecyclerView.getChildAt(i).isFocused();
                if(i==0) focused = Integer.MAX_VALUE;
                if(isFocused) {
                    order = childCount-1;
                    focused = i;
                }
                if(isLast && !isFocused && focused < childCount) {
                    order = focused;
                }
                return order;
            }
        });
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
                View v = mRecyclerView.getFocusedChild();
                if(v == null) mRecyclerView.requestFocus();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.removeBtn)
    public void removeOneItem(View btn) {
        SimpleAdapter adapter = (SimpleAdapter)mRecyclerView.getAdapter();
        adapter.getModel().remove(0);
        adapter.notifyItemRemoved(0);
    }

    static class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {
        private LayoutInflater lf;
        private ArrayList<String> model;

        public SimpleAdapter(Context c) {
            lf = LayoutInflater.from(c);
        }

        public void setModel(String[] m) {
            model = new ArrayList<String>(Arrays.asList(m));
        }

        public ArrayList<String> getModel() {
            return model;
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = lf.inflate(R.layout.list_item, parent, false);
            return new SimpleViewHolder(v);
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
            if(model == null || position >= model.size())
                return;
            String s = model.get(position);
            if(s != null && holder.mText != null) {
                holder.mText.setText(s);
            }
            holder.position = position;
            Log.d(TAG, "onBindViewHolder: "+position+"/"+model.size()+"/"+s);
        }

        @Override
        public int getItemCount() {
            int count = model != null? model.size() : 0;
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
                SimpleViewHolder holder = (SimpleViewHolder) view.getTag();
                if(!view.isAttachedToWindow() || holder == null || holder.mText == null)
                    return;
//                Log.d(TAG, "onFocusChange: "+isFocused+"/"+view);
                if(isFocused)
                    view.bringToFront();
                final float scaleTo = (isFocused? 1.1f : 1.0f);
                view.animate().scaleX(scaleTo).scaleY(scaleTo).setDuration(200).start();
                final float TRANSY = holder.mText.getMeasuredHeight();
                final float textTranslationY = (isFocused? 0 : TRANSY);
                final float textAlphaTo = (isFocused? 1.0f : 0f);
                if(isFocused) {
                    Log.d(TAG, "onFocusChange: trans "+TRANSY+"->"+textTranslationY+", alpha to "+textAlphaTo);
                    holder.mText.animate().translationY(textTranslationY).alpha(textAlphaTo).setDuration(200).start();
                } else {
                    holder.mText.setTranslationY(textTranslationY);
                    holder.mText.setAlpha(textAlphaTo);
                }
            }
        }
    }

}
