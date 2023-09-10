package com.utility.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.utility.app.models.MainMenuResponse;
import com.utility.app.models.ProcedureResponse;
import com.utility.app.models.StepsResponse;
import com.utility.app.models.SurveyResponse;
import com.utility.app.models.request.SurveyRequest;
import com.utility.app.retrofit.ApiClient;
import com.utilityar.app.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakeObservationActivity extends AppCompatActivity {
    private Button btn_next, btn_back;
    String[] priority = {"---Priority---", "High", "Medium", "Low"};
    private Spinner spinnerMenu, spProcedure, spSteps, spPriority;
    private ArrayList<MainMenuResponse> mainmenuList = new ArrayList<>();
    private ArrayList<ProcedureResponse> procedureList = new ArrayList<>();
    private ArrayList<StepsResponse> stepsList = new ArrayList<>();
    private AppCompatEditText edt_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_observation);
        btn_next = findViewById(R.id.btn_next);
        spinnerMenu = findViewById(R.id.sp_mainmenu);
        spProcedure = findViewById(R.id.sp_procedure);
        spSteps = findViewById(R.id.sp_steps);
        btn_back = findViewById(R.id.btn_back);
        edt_comment = findViewById(R.id.edt_comment);

        btn_next.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(edt_comment.getText().toString())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MakeObservationActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
                builder.setTitle("Confirm !").setMessage("Are sure you want to submit task details now?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> submitTask()).setNegativeButton("No", (dialog, which) -> {

                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                edt_comment.setError("Please add Comment before submit");
            }

//            Intent intent = new Intent(getApplicationContext(), CompleteScreenActivity.class);
//            startActivity(intent);
        });
        btn_back.setOnClickListener(v -> {
            finish();
        });
        setupPrioritySpinner();

        getMainMenuList();
        //spinnerMenu.getSelectedItemPosition();
    }

    private void setupMainmenuSpinner() {
        spinnerMenu.setVisibility(View.VISIBLE);
        ArrayAdapter<MainMenuResponse> adapterMainmenu = new ArrayAdapter<MainMenuResponse>(getApplicationContext(), android.R.layout.simple_spinner_item, mainmenuList);
        adapterMainmenu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    getProcedureList(mainmenuList.get(position).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerMenu.setAdapter(adapterMainmenu);
    }

    private void setupProcedureSpinner() {
        spProcedure.setVisibility(View.VISIBLE);
        ArrayAdapter<ProcedureResponse> adapter =
                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, procedureList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spProcedure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    getStespList(procedureList.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spProcedure.setAdapter(adapter);
    }

    private void setupStepsSpinner() {
        spSteps.setVisibility(View.VISIBLE);
        ArrayAdapter<StepsResponse> adapter =
                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, stepsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSteps.setAdapter(adapter);
    }

    private void setupPrioritySpinner() {
        spPriority = findViewById(R.id.sp_priority);
        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, priority);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(ad);
    }


    private void getMainMenuList() {
        mainmenuList.clear();
        ApiClient.getMainMenuList(getApplicationContext(), true).enqueue(new Callback<ArrayList<MainMenuResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<MainMenuResponse>> call, Response<ArrayList<MainMenuResponse>> response) {
                if (response.isSuccessful()) {
                    ArrayList<MainMenuResponse> mainMenuResponses = response.body();
                    if (mainMenuResponses.size() > 0) {
                        MainMenuResponse mainMenuResponse = new MainMenuResponse();
                        mainMenuResponse.setName("---Select Main Menu---");
                        mainmenuList.add(mainMenuResponse);
                        mainmenuList.addAll(mainMenuResponses);
                        setupMainmenuSpinner();
                    } else {
                        spinnerMenu.setVisibility(View.GONE);
                        spProcedure.setVisibility(View.GONE);
                        spSteps.setVisibility(View.GONE);
                    }
                } else {
                }

            }

            @Override
            public void onFailure(Call<ArrayList<MainMenuResponse>> call, Throwable t) {
                spinnerMenu.setVisibility(View.GONE);
            }
        });
    }

    private void getProcedureList(String param) {
        procedureList.clear();
        ApiClient.getProcedureuList(getApplicationContext(), true, param).enqueue(new Callback<ArrayList<ProcedureResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<ProcedureResponse>> call, Response<ArrayList<ProcedureResponse>> response) {
                if (response.isSuccessful()) {
                    ArrayList<ProcedureResponse> mainMenuResponses = response.body();
                    if (mainMenuResponses.size() > 0) {
                        ProcedureResponse procedureResponse = new ProcedureResponse();
                        procedureResponse.setName("---Select Procedure---");
                        procedureList.add(procedureResponse);
                        procedureList.addAll(mainMenuResponses);
                        setupProcedureSpinner();
                    } else {
                        spProcedure.setVisibility(View.GONE);
                        spSteps.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onFailure(Call<ArrayList<ProcedureResponse>> call, Throwable t) {
            }
        });
    }

    private void getStespList(String param) {
        stepsList.clear();
        ApiClient.getStepsList(getApplicationContext(), true, param).enqueue(new Callback<ArrayList<StepsResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<StepsResponse>> call, Response<ArrayList<StepsResponse>> response) {

                if (response.isSuccessful()) {
                    ArrayList<StepsResponse> mainMenuResponses = response.body();
                    if (mainMenuResponses.size() > 0) {
                        StepsResponse stepsResponse = new StepsResponse();
                        stepsResponse.setName("---Select Step---");
                        stepsList.add(stepsResponse);
                        stepsList.addAll(mainMenuResponses);
                        setupStepsSpinner();
                    } else {
                        spSteps.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<StepsResponse>> call, Throwable t) {
                spSteps.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Fail 1", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void submitTask() {
//        progressBar.setVisibility(View.VISIBLE);

        SurveyRequest surveyRequest = new SurveyRequest();

        if (spinnerMenu.getSelectedItemPosition() > 0) {
            surveyRequest.setMenuId(mainmenuList.get(spinnerMenu.getSelectedItemPosition()).getId());
        }
        if (spProcedure.getSelectedItemPosition() > 0) {
            surveyRequest.setProcedureId(procedureList.get(spProcedure.getSelectedItemPosition()).getId());
        }
        if (spSteps.getSelectedItemPosition() > 0) {
            surveyRequest.setStepId(stepsList.get(spSteps.getSelectedItemPosition()).getId());
        }
        if (spPriority.getSelectedItemPosition() > 0) {
            surveyRequest.setPriority(priority[spPriority.getSelectedItemPosition()]);
        }
        surveyRequest.setText(edt_comment.getText().toString());

        btn_next.setEnabled(false);

        ApiClient.createSurvey(getApplicationContext(), false, surveyRequest).enqueue(new Callback<SurveyResponse>() {
            @Override
            public void onResponse(Call<SurveyResponse> call, Response<SurveyResponse> response) {
//                progressBar.setVisibility(View.GONE);
                btn_next.setEnabled(true);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SurveyResponse> call, Throwable t) {
                btn_next.setEnabled(true);
//                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

}