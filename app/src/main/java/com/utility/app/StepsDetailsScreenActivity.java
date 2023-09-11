package com.utility.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
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
        progressBar = findViewById(R.id.progressBar);
        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new StepsPagerAdapter(getApplicationContext(), stepsList, this);
        viewPager.setAdapter(pagerAdapter);
//        viewPager.setUserInputEnabled(false);

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
                    } else {
                    }
                } else {
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
    public void onItemClick(int position) {

    }

    @Override
    public void onItemLongClick(int position) {

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
        Intent intent = new Intent(StepsDetailsScreenActivity.this, MakeObservationActivity2.class);
        intent.putExtra(Constant.stepID,stepsList.get(position).getId());
        startActivity(intent);
    }


}