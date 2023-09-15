package com.utility.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.viewpager2.widget.ViewPager2;

import com.utility.app.adapters.StepsPagerAdapter;
import com.utility.app.listener.OnViewPagerClickListener;
import com.utility.app.models.StepsResponse;
import com.utility.app.retrofit.ApiClient;
import com.utilityar.app.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StepsDetailsScreenActivity extends BaseActivity implements OnViewPagerClickListener {
    //    private Button btn_next, btn_back;
    String procedureID = "";
    private ArrayList<StepsResponse> stepsList = new ArrayList<>();
    ProgressBar progressBar;
    private ViewPager2 viewPager;
    private StepsPagerAdapter pagerAdapter;
    private LinearLayout txt_nodata;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_details_screen);
        Intent myIntent = getIntent();
        procedureID = myIntent.getStringExtra(Constant.procedureID);
        initUI();
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
        pagerAdapter = new StepsPagerAdapter(getApplicationContext(), stepsList, this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setUserInputEnabled(false);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    private void getStespList() {
        stepsList.clear();
        progressBar.setVisibility(View.VISIBLE);
        String param = procedureID;
        ApiClient.getStepsList(getApplicationContext(), true, param).enqueue(new Callback<ArrayList<StepsResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<StepsResponse>> call, Response<ArrayList<StepsResponse>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    ArrayList<StepsResponse> mainMenuResponses = response.body();
                    if (mainMenuResponses.size() > 0) {
                        stepsList.addAll(mainMenuResponses);
                        pagerAdapter.notifyDataSetChanged();
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

        if (position >= stepsList.size() - 1) {
            Intent intent = new Intent(StepsDetailsScreenActivity.this, CompleteScreenActivity.class);
            startActivity(intent);
        } else {
            viewPager.setCurrentItem(position + 1);
        }

    }

    @Override
    public void onBackClick(int position) {
        if (position == 0) {
            finish();
        } else {
            viewPager.setCurrentItem(position - 1);
        }
    }

    @Override
    public void onHomeClick(int position) {
        Intent intent = new Intent(StepsDetailsScreenActivity.this, MainMenuScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onTextButtonClick(int position) {
        Intent intent = new Intent(StepsDetailsScreenActivity.this, MakeObservationActivity.class);
        intent.putExtra(Constant.stepID, stepsList.get(position).getId());
//        intent.putExtra(Constant.SCREEN_FROM_STEPS, true);
        startActivity(intent);
    }

    @Override
    public void onTakePictureClick(int position) {
        Intent intent = new Intent(StepsDetailsScreenActivity.this, MakeObservationActivity.class);
        intent.putExtra(Constant.stepID, stepsList.get(position).getId());
//        intent.putExtra(Constant.SCREEN_FROM_STEPS, true);
//        intent.putExtra(Constant.SCREEN_FROM_STEPS_CAMERA, true);
        startActivity(intent);
    }


}