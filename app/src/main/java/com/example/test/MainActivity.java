package com.example.test;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.test.com.example.test.network.GitHubApi;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final boolean AUTO_ASYNC = true;

    private Call<ResponseBody> mRepos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .build();

        GitHubApi service = retrofit.create(GitHubApi.class);
        mRepos = service.listRepos("wangmuy");

        if(AUTO_ASYNC) {
            autoAsync(mRepos);
        } else {
            manualAsync(mRepos);
        }
    }

    protected void onPause() {
        super.onPause();
        if(mRepos != null && mRepos.isExecuted()) {
            Log.d(TAG, "onPause: isExecuted");
            mRepos.cancel();
        }
    }

    static class RepoCallback implements Callback<ResponseBody> {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            try {
                Log.d(TAG, "onResponse: "+response.body().string());
            } catch (IOException e) {
                Log.e(TAG, "", e);
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.d(TAG, "onFailure: "+t);
        }
    }

    private void autoAsync(final Call<ResponseBody> repos) {
        repos.enqueue(new RepoCallback());
    }

    private void manualAsync(final Call<ResponseBody> repos) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Response<ResponseBody> r = repos.execute();
                    Log.d(TAG, "execute: "+r.body().string());
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
        }.start();
    }
}
