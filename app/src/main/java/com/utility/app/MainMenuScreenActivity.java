package com.utility.app;

import static com.utility.app.Constant.STORAGE_CONTAINER;
import static com.utility.app.Constant.connectionString;
import static com.utility.app.FileUtils.getFile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.utility.app.adapters.MainmenuAdapter;
import com.utility.app.listener.OnItemClickListener;
import com.utility.app.models.MainMenuResponse;
import com.utility.app.retrofit.ApiClient;
import com.utilityar.app.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//TODO https://www.digitalocean.com/community/tutorials/android-recyclerview-load-more-endless-scrolling

public class MainMenuScreenActivity extends AppCompatActivity {


    Uri imageUri = null;
    private ArrayList<MainMenuResponse> mainmenuList = new ArrayList<>();
    private RecyclerView courseRV;
    private MainmenuAdapter mainmenuAdapter;
    private Button btn_make_observation, btnChangeUser;
    private TextView txtNodata;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_menu);
        initUI();

        // on below line we are adding our array list to our adapter class.
        mainmenuAdapter = new MainmenuAdapter(this, mainmenuList, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getApplicationContext(), ProcedureScreenActivity.class);
                intent.putExtra(Constant.menuID,mainmenuList.get(position).getId());
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

    }

    private void initUI() {
        // initializing our variables.
        btnChangeUser = findViewById(R.id.btn_change_user);
        btn_make_observation = findViewById(R.id.btn_make_observation);
        courseRV = findViewById(R.id.idRVCourses);
        txtNodata = findViewById(R.id.txt_nodata);
        btn_make_observation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), ProcedureScreenActivity.class);
                Intent intent = new Intent(getApplicationContext(), FinalObservationActivity.class);
                startActivity(intent);
            }
        });

        btnChangeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager.getInstance().logout();
                Intent intent = new Intent(getApplicationContext(), LoginScreenActivity.class);
                startActivity(intent);
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> getMainMenuList());

    }



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
                        mainmenuAdapter.notifyDataSetChanged();
                        txtNodata.setVisibility(View.GONE);
                    } else {
                        txtNodata.setVisibility(View.VISIBLE);
                        courseRV.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(MainMenuScreenActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    txtNodata.setVisibility(View.VISIBLE);
                    courseRV.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<MainMenuResponse>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                txtNodata.setVisibility(View.VISIBLE);
                courseRV.setVisibility(View.VISIBLE);
                Toast.makeText(MainMenuScreenActivity.this, "Fail 1", Toast.LENGTH_SHORT).show();
            }
        });
    }




}