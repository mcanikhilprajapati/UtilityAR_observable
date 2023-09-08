package com.utility.app.retrofit;

import android.content.Context;

import com.utility.app.App;
import com.utility.app.models.MainMenuResponse;
import com.utility.app.models.ProcedureResponse;
import com.utility.app.models.StepsResponse;
import com.utility.app.models.SurveyResponse;
import com.utility.app.models.request.LoginRequest;
import com.utility.app.models.LoginResponse;
import com.utility.app.models.TokenResponse;
import com.utility.app.models.request.ProcedureRequest;
import com.utility.app.models.request.SurveyRequest;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;

// https://gist.github.com/dinukapj/b315e5f4438bc670d7509f7aa7aaffdd
public class ApiClient {
    private static ApiInterface getClient() {
        return App.retrofit.create(ApiInterface.class);
    }

    /* Login api to get access and check if user registered or not */
    public static Call<LoginResponse> loginAPI(Context context, boolean showLoader, LoginRequest params) {
        return getClient().login(params);
    }
    public static Call<TokenResponse> getToken(Context context, boolean showLoader) {
        return getClient().getToken();
    }

    public static Call<ArrayList<MainMenuResponse>> getMainMenuList(Context context, boolean showLoader) {
        return getClient().getMainMenuList();
    }
    public static Call<ArrayList<ProcedureResponse>> getProcedureuList(Context context, boolean showLoader, String data ) {
        return getClient().getProcedureList(data);
    }
    public static Call<ArrayList<StepsResponse>> getStepsList(Context context, boolean showLoader, String data ) {
        return getClient().getStepsList(data);
    }
    public static Call<SurveyResponse> createSurvey(Context context, boolean showLoader, SurveyRequest surveyRequest) {
        return getClient().createSurvey(surveyRequest);
    }
}