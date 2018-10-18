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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
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
    private static final int TOTAL_ATTEMPT = 3;

    private String mTextContent = "";
    private TextView mTextView;
    private Button mCallBtn;
    private Button mRxBtn;

    private GitHubApi mGithubService;
    private Call<List<Repo>> mCallRepos;

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

    private PublishSubject<View> mRxBtnClickSubject = PublishSubject.create();
    private CompositeDisposable mAllDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        mCallBtn = (Button) findViewById(R.id.callBtn);
        mCallBtn.setOnClickListener(mCallBtnListener);
        mRxBtn = (Button) findViewById(R.id.rxBtn);
        mRxBtn.setOnClickListener(v -> mRxBtnClickSubject.onNext(v));

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
        mTextContent = "";
        mTextView.setText(mTextContent);

        // https://stackoverflow.com/questions/42634508/prevent-multiple-api-calls
        Disposable d = mRxBtnClickSubject
            .doOnNext(view -> Log.d(TAG, "--[RX]-- clicked"))
            .take(1)
            .doOnNext(view -> Log.d(TAG, "--[RX]-- click taked"))
            .observeOn(Schedulers.io())
            .flatMap(view ->
                mGithubService.rxListRepos(USER).toObservable()
                    .doOnSubscribe((disposable) -> Log.d(TAG, "--[RX]-- netreq onSubscribe: tid="+Thread.currentThread().toString()))
                    .doOnError((e) -> Log.e(TAG, "--[RX]-- netreq onError: tid="+Thread.currentThread().toString(), e))
                    .retryWhen(throwableObs ->  // exponential back off
                        throwableObs
                        .zipWith(Observable.range(1, TOTAL_ATTEMPT), (throwable, attemptCount) ->
                            attemptCount < TOTAL_ATTEMPT?
                                Observable.timer(1 << (attemptCount-1), TimeUnit.SECONDS):
                                Observable.error(throwable))
                        .flatMap(x -> x)
                    ))
            .firstOrError()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe((disposable) -> Log.d(TAG, "--[RX]-- onSubscribe: tid="+Thread.currentThread().toString()))
            .doOnSuccess(repos -> Log.d(TAG, "--[RX]-- onSuccess: got repos, tid="+Thread.currentThread().toString()))
            .doOnError((throwable) -> {
                Log.e(TAG, "--[RX]-- onError: tid="+Thread.currentThread().toString(), throwable);
                RepoCallback.handleError(throwable, MainActivity.this);
            })
            .repeat()
            .retry()
            .subscribe(
                (repos) -> RepoCallback.handleList(repos, MainActivity.this),
                (error) -> RepoCallback.handleError(error, MainActivity.this) // not called
            );
        mAllDisposable.add(d);
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
            Log.d(TAG, "handleList");
            StringBuilder sb = new StringBuilder();
            for (Repo r : repos) {
                sb.append(r.full_name).append("\n");
            }
            sb.append("\n");
            final String repoListText = sb.toString();
            activity.mTextContent += repoListText;
            activity.mTextView.setText(activity.mTextContent);
        }

        public static void handleError(Throwable t, MainActivity activity) {
            Log.d(TAG, "handleError");
            activity.mTextContent = t.toString();
            activity.mTextView.setText(activity.mTextContent);
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
