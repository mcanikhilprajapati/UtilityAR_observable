package com.utility.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.viewpager2.widget.ViewPager2;

import com.utility.app.adapters.StepsFragmentStateAdapter;
import com.utility.app.listener.OnViewPagerClickListener;
import com.utility.app.models.StepsResponse;
import com.utility.app.models.request.Useractivity;
import com.utility.app.retrofit.ApiClient;
import com.utilityar.app.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StepsDetailsScreenActivity extends BaseActivity implements OnViewPagerClickListener {
    private String procedureID = "";
    private String procedureName = "";
    private String menuID = "";
    private String menuName = "";
    private ProgressBar progressBar;
    private ViewPager2 viewPager;
    private StepsFragmentStateAdapter stepsFragmentStateAdapter;
    private LinearLayout txt_nodata;
    private Button btn_back;
    public static ArrayList<StepsResponse> globlestepsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_steps_details_screen);
        Intent myIntent = getIntent();
        procedureName = myIntent.getStringExtra(Constant.procedureName);
        procedureID = myIntent.getStringExtra(Constant.procedureID);
        menuID = myIntent.getStringExtra(Constant.menuID);
        menuName = myIntent.getStringExtra(Constant.menuName);
        initUI();

        Useractivity useractivity = new Useractivity("Steps Screen",Constant.userTrackerAction.SCREEN_OPEN.toString());
        trackUserActivity( useractivity);
        getStespList();
    }


    private void initUI() {
        btn_back = findViewById(R.id.btn_back);
        txt_nodata = findViewById(R.id.txt_nodata);
        progressBar = findViewById(R.id.progressBar);
        btn_back.setOnClickListener(v -> {
            finish();
        });

        viewPager = findViewById(R.id.viewPager);
        stepsFragmentStateAdapter = new StepsFragmentStateAdapter(this, globlestepsList, menuName,procedureName,this);
        viewPager.setAdapter(stepsFragmentStateAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setUserInputEnabled(false);

    }
    /* < GET LIST OF STEPS> */
    private void getStespList() {
        globlestepsList.clear();
        progressBar.setVisibility(View.VISIBLE);
        String param = procedureID;
        ApiClient.getStepsList(getApplicationContext(), true, param).enqueue(new Callback<ArrayList<StepsResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<StepsResponse>> call, Response<ArrayList<StepsResponse>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    ArrayList<StepsResponse> mainMenuResponses = response.body();
                    if (mainMenuResponses.size() > 0) {
                        globlestepsList.addAll(mainMenuResponses);
                        stepsFragmentStateAdapter.notifyDataSetChanged();
                        txt_nodata.setVisibility(View.GONE);
                    } else {
                        txt_nodata.setVisibility(View.VISIBLE);
                    }
                } else {
                    txt_nodata.setVisibility(View.VISIBLE);
                    Toast.makeText(StepsDetailsScreenActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<StepsResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(StepsDetailsScreenActivity.this, "Fail 1", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onNextClick(int position) {
        Useractivity useractivity = new Useractivity(globlestepsList.get(position).getName(),Constant.userTrackerAction.BUTTON_CLICKED.toString(),"NEXT");
        trackUserActivity( useractivity);
        if (position >= globlestepsList.size() - 1) {
            Intent intent = new Intent(StepsDetailsScreenActivity.this, CompleteScreenActivity.class);
            startActivity(intent);
        } else {
            viewPager.setCurrentItem(position + 1);
        }

    }

    @Override
    public void onBackClick(int position) {
        Useractivity useractivity = new Useractivity(globlestepsList.get(position).getName(),Constant.userTrackerAction.BUTTON_CLICKED.toString(),"BACK");
        trackUserActivity( useractivity);
        if (position == 0) {
            finish();
        } else {
            viewPager.setCurrentItem(position - 1);
        }
    }

    @Override
    public void onHomeClick(int position) {
        Useractivity useractivity = new Useractivity(globlestepsList.get(position).getName(),Constant.userTrackerAction.BUTTON_CLICKED.toString(),"HOME");
        trackUserActivity( useractivity);
        Intent intent = new Intent(StepsDetailsScreenActivity.this, MainMenuScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    @Override
    public void onButtonClick(int position, boolean isForImage) {

        Useractivity useractivity = new Useractivity(globlestepsList.get(position).getName(),Constant.userTrackerAction.BUTTON_CLICKED.toString(),isForImage?"CAMERA" :"TEXT");
        trackUserActivity( useractivity);

        Intent intent = new Intent(StepsDetailsScreenActivity.this, MakeObservationActivity.class);
        intent.putExtra(Constant.stepID, globlestepsList.get(position).getId());
        intent.putExtra(Constant.menuID, menuID);
        intent.putExtra(Constant.procedureID, procedureID);
        intent.putExtra(Constant.SCREEN_FROM_STEPS, true);
        intent.putExtra(Constant.STEP_INDEX, position);
        intent.putExtra(Constant.SCREEN_FROM_STEPS_CAMERA, isForImage);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepsFragmentStateAdapter != null) {

            //to update status of observations action taken
            stepsFragmentStateAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        globlestepsList.clear();

    }
}