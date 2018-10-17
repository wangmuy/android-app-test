package com.example.test;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.example.test.model.Repo;
import com.example.test.network.GitHubApi;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final boolean AUTO_ASYNC = false;

    private TextView mTextView;
    private Call<List<Repo>> mRepos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubApi service = retrofit.create(GitHubApi.class);
        mRepos = service.listRepos("wangmuy");

        if (AUTO_ASYNC) {
            mRepos.enqueue(new RepoCallback(this));
        } else {
            new ManualThread(mRepos, this).start();
        }
    }

    protected void onPause() {
        super.onPause();
        if (mRepos != null && mRepos.isExecuted()) {
            Log.d(TAG, "onPause: isExecuted");
            mRepos.cancel();
        }
    }

    static class RepoCallback implements Callback<List<Repo>> {
        private WeakReference<MainActivity> activityRef;

        public RepoCallback(MainActivity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
            List<Repo> repos = response.body();
            Log.d(TAG, "onResponse: " + response);
            StringBuilder sb = new StringBuilder();
            for (Repo r : repos) {
                sb.append(r.toString());
            }
            final String repoListText = sb.toString();
            MainActivity activity = activityRef.get();
            if (activity != null) {
                activity.mTextView.setText(repoListText);
            }
        }

        @Override
        public void onFailure(Call<List<Repo>> call, Throwable t) {
            Log.d(TAG, "onFailure: " + t);
            MainActivity activity = activityRef.get();
            if (activity != null) {
                activity.mTextView.setText(t.toString());
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
