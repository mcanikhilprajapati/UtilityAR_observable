package com.utility.app;

import static com.utility.app.SessionManager.JWT;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.utilityar.app.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class App extends Application {
    String TAG = App.class.getSimpleName();
    public static final String BASE_URL = Constant.BaseURL;
    public static Retrofit retrofit = null;

    @Override
    public void onCreate() {
        super.onCreate();

        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl"
        );
        new SessionManager(this);
        retrofitSetup();
    }

    private void retrofitSetup() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        httpClient.writeTimeout(60, TimeUnit.SECONDS);
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(interceptor);
        }

        httpClient.addInterceptor(chain -> {
            if (BuildConfig.DEBUG) {
                Log.d("Token=> Bearer ", SessionManager.pref.getString(JWT, ""));
            }
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + SessionManager.pref.getString(JWT, "")).method(original.method(), original.body());
            Request request = requestBuilder.build();

            return chain.proceed(request);
        });
        httpClient.addInterceptor(new UnauthorizedInterceptor());
        OkHttpClient client = httpClient.build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
    class UnauthorizedInterceptor implements Interceptor {

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            if (response.code() == 401) {
                Log.d("Lougout","Login fail "+response.code());
            }
            return response;
        }
    }
}
