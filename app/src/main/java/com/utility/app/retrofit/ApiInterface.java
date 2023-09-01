package com.utility.app.retrofit;


import com.utility.app.models.LoginResponse;
import com.utility.app.models.TokenResponse;
import com.utility.app.models.request.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    // 1 -----------------------------------firmwareUpdate------------------------------------------
    @Headers({ "Content-Type: application/json;charset=UTF-8"})

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest params);

    @GET("auth/token")
    Call<TokenResponse> getToken(@Header("Authorization") String authHeader);



}
