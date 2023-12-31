package com.utility.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.utility.app.adapters.MainmenuAdapter;
import com.utility.app.listener.OnItemClickListener;
import com.utility.app.models.MainMenuResponse;
import com.utility.app.models.request.Useractivity;
import com.utility.app.retrofit.ApiClient;
import com.utilityar.app.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//TODO https://www.digitalocean.com/community/tutorials/android-recyclerview-load-more-endless-scrolling

public class MainMenuScreenActivity extends BaseActivity {


    private ArrayList<MainMenuResponse> mainmenuList = new ArrayList<>();
    private RecyclerView courseRV;
    private MainmenuAdapter mainmenuAdapter;
    private Button btn_make_observation, btnChangeUser;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);
        initUI();

        // on below line we are adding our array list to our adapter class.
        mainmenuAdapter = new MainmenuAdapter(this, mainmenuList, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Useractivity useractivity = new Useractivity(mainmenuList.get(position).getName(),Constant.userTrackerAction.SCREEN_OPEN.toString());
                trackUserActivity( useractivity);

                Intent intent = new Intent(getApplicationContext(), ProcedureScreenActivity.class);
                intent.putExtra(Constant.menuID, mainmenuList.get(position).getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        courseRV.setLayoutManager(manager);
        courseRV.setAdapter(mainmenuAdapter);
        getMainMenuList();

        Useractivity useractivity = new Useractivity("Mainmenu",Constant.userTrackerAction.SCREEN_OPEN.toString());
        trackUserActivity( useractivity);
    }



    private void initUI() {
        // initializing our variables.
        AppCompatTextView toolbarTitle = findViewById(R.id.txt_toolbar_title);
        toolbarTitle.setText("Main Menu");
        btnChangeUser = findViewById(R.id.btn_change_user);
        btn_make_observation = findViewById(R.id.btn_make_observation);
        courseRV = findViewById(R.id.idRVCourses);
        btn_make_observation.setOnClickListener(v -> {
//                Intent intent = new Intent(getApplicationContext(), ProcedureScreenActivity.class);
            Intent intent = new Intent(getApplicationContext(), MakeObservationActivity.class);
            startActivity(intent);
        });

        btnChangeUser.setOnClickListener(v -> {
            SessionManager.getInstance().logout();
            Intent intent = new Intent(getApplicationContext(), LoginScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> getMainMenuList());

    }


    /* < GET LIST OF MAIN MENU BASED ON USER> */
    private void getMainMenuList() {
        mainmenuList.clear();
        swipeRefreshLayout.setRefreshing(true);
        ApiClient.getMainMenuList(getApplicationContext(), true).enqueue(new Callback<ArrayList<MainMenuResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<MainMenuResponse>> call, Response<ArrayList<MainMenuResponse>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    ArrayList<MainMenuResponse> mainMenuResponses = response.body();
                    if (mainMenuResponses.size() > 0) {
                        mainmenuList.addAll(mainMenuResponses);
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        mainmenuAdapter.notifyDataSetChanged();

                    } else {
                        courseRV.setVisibility(View.GONE);
                    }
                } else {
                    courseRV.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<MainMenuResponse>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                courseRV.setVisibility(View.GONE);
            }
        });
    }


}
