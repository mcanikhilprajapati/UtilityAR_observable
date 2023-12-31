package com.utility.app.retrofit;


import com.utility.app.models.LoginResponse;
import com.utility.app.models.MainMenuResponse;
import com.utility.app.models.ProcedureResponse;
import com.utility.app.models.StepsResponse;
import com.utility.app.models.SurveyResponse;
import com.utility.app.models.TokenResponse;
import com.utility.app.models.request.LoginRequest;
import com.utility.app.models.request.SurveyRequest;
import com.utility.app.models.request.Useractivity;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

    @GET("steps/{pid}")
    Call<ArrayList<StepsResponse>> getStepsList(@Path("pid") String id);

    @POST("completedSurvey/create")
    Call<SurveyResponse> createSurvey(@Body SurveyRequest params);

    @POST("userActivity/create")
    Call<ResponseBody> userActivityTracker(@Body Useractivity params);
}
