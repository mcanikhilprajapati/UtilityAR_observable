package com.utility.app.retrofit;


import com.utility.app.models.LoginResponse;
import com.utility.app.models.MainMenuResponse;
import com.utility.app.models.ProcedureResponse;
import com.utility.app.models.TokenResponse;
import com.utility.app.models.request.LoginRequest;
import com.utility.app.models.request.ProcedureRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    // 1 -----------------------------------firmwareUpdate------------------------------------------
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest params);

    @GET("auth/token")
    Call<TokenResponse> getToken();

    @GET("mainmenu")
    Call<ArrayList<MainMenuResponse>> getMainMenuList();

    @GET("procedure/{menuId}")
    Call<ArrayList<ProcedureResponse>> getProcedureList(@Path("menuId") String id);

}
