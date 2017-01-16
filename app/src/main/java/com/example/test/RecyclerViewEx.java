package com.example.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;


public class RecyclerViewEx extends RecyclerView {
    private static final String TAG = "RecyclerViewEx";
    private int mEmptyViewId;
    private View mEmptyView;

    public RecyclerViewEx(Context context) {
        this(context, null, 0);
    }

    public RecyclerViewEx(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewEx(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RecyclerViewEx, 0, 0);
        try {
            mEmptyViewId = a.getResourceId(R.styleable.RecyclerViewEx_emptyView, 0);
            Log.d(TAG, "init emptyViewId="+mEmptyViewId);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent p = getParent();
        if(p != null && p instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup)p;
            if(mEmptyViewId != 0) {
                mEmptyView = parent.findViewById(mEmptyViewId);
                Log.d(TAG, "onAttachedToWindow: emptyView="+mEmptyView);
                checkIfEmpty();
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter old = getAdapter();
        if(old != null) {
            old.unregisterAdapterDataObserver(mObserver);
        }
        if(adapter != null)
            adapter.registerAdapterDataObserver(mObserver);
        super.setAdapter(adapter);
        checkIfEmpty();
    }

    @Override
    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        final Adapter old = getAdapter();
        if(old != null)
            old.unregisterAdapterDataObserver(mObserver);
        if(adapter != null)
            adapter.registerAdapterDataObserver(mObserver);
        super.swapAdapter(adapter, removeAndRecycleExistingViews);
        checkIfEmpty();
    }

    public void setEmptyView(@Nullable View emptyView) {
        mEmptyView = emptyView;
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        if(mEmptyView == null)
            return;
        boolean visible = (getAdapter() == null || getAdapter().getItemCount() <= 0);
        mEmptyView.setVisibility(visible? View.VISIBLE : View.GONE);
    }

    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkIfEmpty();
        }
    };

}
