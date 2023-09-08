package com.utility.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.utility.app.models.MainMenuResponse;
import com.utility.app.retrofit.ApiClient;
import com.utilityar.app.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakeObservationActivity extends AppCompatActivity {
    private Button btn_next, btn_back;
    String[] priority = {"Priority", "High", "Medium", "Low"};
    private Spinner spinnerMenu;
    private ArrayList<MainMenuResponse> mainmenuList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_observation);
        btn_next = findViewById(R.id.btn_next);
        spinnerMenu = findViewById(R.id.sp_mainmenu);
        btn_back = findViewById(R.id.btn_back);
        btn_next.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CompleteScreenActivity.class);
            startActivity(intent);
        });
        btn_back.setOnClickListener(v -> {
            finish();
        });
        setupPrioritySpinner();

        getMainMenuList();
    }

    private void setupMainmenuSpinner() {

        ArrayAdapter<MainMenuResponse> adapter =
                new ArrayAdapter<MainMenuResponse>(getApplicationContext(), android.R.layout.simple_spinner_item, mainmenuList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    Toast.makeText(MakeObservationActivity.this, mainmenuList.get(position).getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerMenu.setAdapter(adapter);
    }

    private void setupPrioritySpinner() {
        Spinner spino = findViewById(R.id.sp_priority);
        spino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, priority);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spino.setAdapter(ad);
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
                        mainMenuResponse.setName("Select Main Menu");
                        mainmenuList.add(mainMenuResponse);
                        mainmenuList.addAll(mainMenuResponses);
                        setupMainmenuSpinner();
                    } else {
                    }
                } else {
                }

            }

            @Override
            public void onFailure(Call<ArrayList<MainMenuResponse>> call, Throwable t) {
            }
        });
    }

}