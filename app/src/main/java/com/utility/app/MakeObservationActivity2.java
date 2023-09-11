package com.utility.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.utility.app.models.SurveyResponse;
import com.utility.app.models.request.SurveyRequest;
import com.utility.app.retrofit.ApiClient;
import com.utilityar.app.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakeObservationActivity2 extends BaseActivity {
    private Button btn_next, btn_back;
    private AppCompatEditText edt_comment;
    String stepID = "";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_observation2);

        Intent myIntent = getIntent();
        stepID = myIntent.getStringExtra(Constant.stepID);

        progressBar = findViewById(R.id.progressBar);
        btn_next = findViewById(R.id.btn_next);
        btn_back = findViewById(R.id.btn_back);
        edt_comment = findViewById(R.id.edt_comment);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edt_comment.getText().toString())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MakeObservationActivity2.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
                    builder.setTitle("Confirm !").setMessage("Are sure you want to submit task details now?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> submitTask()).setNegativeButton("No", (dialog, which) -> {

                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    edt_comment.setError("Please add Comment before submit");
                }
            }
        });
        btn_back.setOnClickListener(v -> finish());
    }

    private void submitTask() {
        progressBar.setVisibility(View.VISIBLE);
        btn_next.setEnabled(false);
        SurveyRequest surveyRequest = new SurveyRequest();
        surveyRequest.setStepId(stepID);
        surveyRequest.setText(edt_comment.getText().toString());
        ApiClient.createSurvey(getApplicationContext(), false, surveyRequest).enqueue(new Callback<SurveyResponse>() {
            @Override
            public void onResponse(Call<SurveyResponse> call, Response<SurveyResponse> response) {
                progressBar.setVisibility(View.GONE);
                btn_next.setEnabled(true);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Toast.makeText(MakeObservationActivity2.this, "Submitted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(MakeObservationActivity2.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MakeObservationActivity2.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SurveyResponse> call, Throwable t) {
                btn_next.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MakeObservationActivity2.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }


}