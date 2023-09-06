package com.utility.app;


import static com.utility.app.SessionManager.CHATTHREAD_ID;
import static com.utility.app.SessionManager.COMMUNICATIONUSERID;
import static com.utility.app.SessionManager.EMAIL;
import static com.utility.app.SessionManager.GROUPCALL_ID;
import static com.utility.app.SessionManager.JWT;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.utility.app.models.LoginResponse;
import com.utility.app.models.TokenResponse;
import com.utility.app.models.request.LoginRequest;
import com.utility.app.retrofit.ApiClient;
import com.utilityar.app.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreenActivity extends AppCompatActivity implements View.OnClickListener {
    AppCompatButton btnLogin,btnHeadsetMode;
    AppCompatEditText edtUsername, edtPassword;
    ProgressBar progressBar;
    LinearLayoutCompat main_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login_screen);


        // init UI
        initUI();

        //Check for stored session and refresh token if already logded in.
        if (SessionManager.pref.getString(JWT, "") != "") {
            main_container.setVisibility(View.GONE);
            getToken(SessionManager.pref.getString(JWT, ""));
        } else {
            edtUsername.setText(SessionManager.pref.getString(EMAIL, ""));
            edtUsername.requestFocus();
        }


    }

    private void initUI() {
        main_container = findViewById(R.id.main_container);
        progressBar = findViewById(R.id.progressBar);
        btnLogin = findViewById(R.id.btn_login);
        btnHeadsetMode = findViewById(R.id.btn_headset_mode);
        edtPassword = findViewById(R.id.edt_password);
        edtUsername = findViewById(R.id.edt_username);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (validation()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    doLogin();
                }
                break;

        }
    }

    private void doLogin() {

        progressBar.setVisibility(View.VISIBLE);

        btnLogin.setVisibility(View.GONE);
        btnHeadsetMode.setVisibility(View.GONE);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(edtUsername.getText().toString().trim());
        loginRequest.setPassword(edtPassword.getText().toString().trim());


        ApiClient.loginAPI(LoginScreenActivity.this, true, loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {
                        SessionManager.getInstance().setValue(COMMUNICATIONUSERID, loginResponse.getCommunicationUserId());
                        SessionManager.getInstance().setValue(CHATTHREAD_ID, loginResponse.getChatThreadId());
                        SessionManager.getInstance().setValue(JWT, loginResponse.getJwt());
                        SessionManager.getInstance().setValue(GROUPCALL_ID, loginResponse.getGroupCallId());
                        SessionManager.getInstance().setValue(EMAIL, loginResponse.getEmail());
                        getToken(loginResponse.getJwt());
                    } else {
                        Toast.makeText(LoginScreenActivity.this, "Something went wrong at login", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                    btnHeadsetMode.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginScreenActivity.this, "Please Provide Correct Username And Password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setVisibility(View.VISIBLE);
                btnHeadsetMode.setVisibility(View.VISIBLE);
                Toast.makeText(LoginScreenActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //    //Get new access token everytime when user open app
    private void getToken(String t) {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getToken(getApplicationContext(), true).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful()) {
                    TokenResponse tokenResponse = response.body();
                    if (tokenResponse != null) {
                        //Store token in local storage
                        SessionManager.getInstance().setValue(SessionManager.TOKEN, tokenResponse.getToken());
                        //Let user move to next login screen
                        gotoLoginScreen();
                    }
                }

            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                main_container.setVisibility(View.VISIBLE);
            }
        });
    }

    private void gotoLoginScreen() {
        Intent intent = new Intent(getApplicationContext(), MainMenuScreenActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validation() {
        if (TextUtils.isEmpty(edtUsername.getText().toString())) {
            edtUsername.setError("Email require");
            edtUsername.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
            edtPassword.setError("Password require");
            edtPassword.requestFocus();
            return false;
        }
        return true;

    }


}