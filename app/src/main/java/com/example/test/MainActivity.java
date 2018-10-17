package com.example.test;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.test.model.Repo;
import com.example.test.network.GitHubApi;
import com.squareup.haha.perflib.Main;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.subscriptions.ArrayCompositeSubscription;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final boolean AUTO_ASYNC = false;
    private static final String USER = "wangmuy";

    private TextView mTextView;
    private Button mCallBtn;
    private Button mRxBtn;

    private GitHubApi mGithubService;
    private Call<List<Repo>> mCallRepos;
    private CompositeDisposable mAllDisposable = new CompositeDisposable();

    private View.OnClickListener mCallBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallRepos = mGithubService.listRepos(USER);

            if (AUTO_ASYNC) {
                mCallRepos.enqueue(new RepoCallback(MainActivity.this));
            } else {
                new ManualThread(mCallRepos, MainActivity.this).start();
            }
        }
    };

    private View.OnClickListener mRxBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Disposable d = mGithubService.rxListRepos(USER)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            (repos) -> RepoCallback.handleList(repos, MainActivity.this),
                            (error) -> RepoCallback.handleError(error, MainActivity.this)
                    );
            mAllDisposable.add(d);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        mCallBtn = (Button) findViewById(R.id.callBtn);
        mCallBtn.setOnClickListener(mCallBtnListener);
        mRxBtn = (Button) findViewById(R.id.rxBtn);
        mRxBtn.setOnClickListener(mRxBtnListener);

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
        mGithubService = retrofit.create(GitHubApi.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
        if (mCallRepos != null && mCallRepos.isExecuted()) {
            Log.d(TAG, "onPause: isExecuted");
            mCallRepos.cancel();
        }
        mAllDisposable.clear();
    }

    static class RepoCallback implements Callback<List<Repo>> {
        private WeakReference<MainActivity> activityRef;

        public RepoCallback(MainActivity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        public static void handleList(List<Repo> repos, MainActivity activity) {
            StringBuilder sb = new StringBuilder();
            for (Repo r : repos) {
                sb.append(r.toString());
            }
            final String repoListText = sb.toString();
            activity.mTextView.setText(repoListText);
        }

        public static void handleError(Throwable t, MainActivity activity) {
            activity.mTextView.setText(t.toString());
        }

        @Override
        public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
            List<Repo> repos = response.body();
            Log.d(TAG, "onResponse: " + response);
            MainActivity activity = activityRef.get();
            if (activity != null) {
                handleList(repos, activity);
            }
        }

        @Override
        public void onFailure(Call<List<Repo>> call, Throwable t) {
            Log.d(TAG, "onFailure: " + t);
            MainActivity activity = activityRef.get();
            if (activity != null) {
                handleError(t, activity);
            }
        }
    }

    private static class ManualThread extends Thread {
        private WeakReference<MainActivity> activityRef;
        private Call<List<Repo>> repos;

        public ManualThread(Call<List<Repo>> repos, MainActivity acitivty) {
            this.repos = repos;
            this.activityRef = new WeakReference<>(acitivty);
        }

        @Override
        public void run() {
            try {
                final Response<List<Repo>> r = repos.execute();
                MainActivity activity = activityRef.get();
                if(activity != null) {
                    final RepoCallback cb = new RepoCallback(activity);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cb.onResponse(repos, r);
                        }
                    });
                }
            } catch(final IOException e) {
                MainActivity activity = activityRef.get();
                if(activity != null) {
                    final RepoCallback cb = new RepoCallback(activity);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cb.onFailure(repos, e);
                        }
                    });
                }
            }
        }
    }
}
