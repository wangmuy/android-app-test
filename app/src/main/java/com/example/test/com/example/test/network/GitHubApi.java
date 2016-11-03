package com.example.test.com.example.test.network;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GitHubApi {
    @GET("users/{user}/repos")
    Call<ResponseBody> listRepos(@Path("user") String user);
}
