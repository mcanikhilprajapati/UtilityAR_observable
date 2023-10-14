package com.utility.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.utility.app.adapters.ProcedureAdapter;
import com.utility.app.listener.OnItemClickListener;
import com.utility.app.models.ProcedureResponse;
import com.utility.app.models.request.Useractivity;
import com.utility.app.retrofit.ApiClient;
import com.utilityar.app.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProcedureScreenActivity extends BaseActivity {


    private ArrayList<ProcedureResponse> procedureList = new ArrayList<>();
    private RecyclerView rvProcedure;
    private ProcedureAdapter procedureAdapter;
    private Button btnBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private  String menuID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_procedure);
        Intent myIntent = getIntent();
        menuID = myIntent.getStringExtra(Constant.menuID);
        initUI();

        procedureAdapter = new ProcedureAdapter(this, procedureList, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Useractivity useractivity = new Useractivity(procedureList.get(position).getName(),Constant.userTrackerAction.SCREEN_OPEN.toString());
                trackUserActivity( useractivity);
                Intent intent = new Intent(getApplicationContext(), StepsDetailsScreenActivity.class);
                intent.putExtra(Constant.procedureID,procedureList.get(position).getId());
                intent.putExtra(Constant.menuID,menuID);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvProcedure.setLayoutManager(manager);
        rvProcedure.setAdapter(procedureAdapter);
        getProcedureList();

    }

    private void initUI() {
        AppCompatTextView toolbarTitle = findViewById(R.id.txt_toolbar_title);
        toolbarTitle.setText("Procedures / Data Menu");

        // initializing our variables.
        btnBack = findViewById(R.id.btn_back);
        rvProcedure = findViewById(R.id.idRVCourses);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> getProcedureList());
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }

    /* < GET LIST OF PROCEDURES> */
    private void getProcedureList() {
        procedureList.clear();
        swipeRefreshLayout.setRefreshing(true);
        String param = menuID;
        ApiClient.getProcedureuList(getApplicationContext(), true, param).enqueue(new Callback<ArrayList<ProcedureResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<ProcedureResponse>> call, Response<ArrayList<ProcedureResponse>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    ArrayList<ProcedureResponse> mainMenuResponses = response.body();
                    if (mainMenuResponses.size() > 0) {
                        procedureList.addAll(mainMenuResponses);
                        procedureAdapter.notifyDataSetChanged();
                    } else {
                        rvProcedure.setVisibility(View.GONE);
                    }
                } else {
                    rvProcedure.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<ProcedureResponse>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                rvProcedure.setVisibility(View.GONE);
            }
        });
    }


}
