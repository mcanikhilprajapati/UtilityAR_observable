package com.utility.app;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static com.utility.app.StepsDetailsScreenActivity.globlestepsList;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.utility.app.listener.OnFileUploadListner;
import com.utility.app.models.MainMenuResponse;
import com.utility.app.models.ProcedureResponse;
import com.utility.app.models.StepsResponse;
import com.utility.app.models.SurveyResponse;
import com.utility.app.models.request.SurveyRequest;
import com.utility.app.models.request.Useractivity;
import com.utility.app.retrofit.ApiClient;
import com.utilityar.app.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakeObservationActivity extends BaseActivity implements OnFileUploadListner {
    private Button btn_next, btn_back, btn_camera;
    String[] priority = {"---Priority---", "High", "Medium", "Low"};
    private Spinner spinnerMenu, spProcedure, spSteps, spPriority;
    private ArrayList<MainMenuResponse> mainmenuList = new ArrayList<>();
    private ArrayList<ProcedureResponse> procedureList = new ArrayList<>();
    private ArrayList<StepsResponse> stepsList = new ArrayList<>();

    ArrayAdapter<MainMenuResponse> adapterMainmenu;
    ArrayAdapter<ProcedureResponse> adapterProcedure;
    ArrayAdapter<StepsResponse> adapterSteps;
    private ExoPlayer player;
    private AppCompatEditText edt_comment;
    private Uri fileURI;
    private String selectedMedia;
    private String selectMediaType = "";
    private AppCompatImageView cameraImage;
    private ProgressBar progressBar;
    private PlayerView playerView;


    String stepID = "";
    String procedureID = "";
    String menuID = "";
    boolean isFromStepScreen = false;
    boolean isFromImageScreen = false;
    int stepIndex = -1;
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    selectMediaType = Constant.IMAGE;
                    Bitmap thumbnail = null;
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), fileURI);
                    } catch (IOException e) {
                    }
                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(FileUtils.getFile(getApplicationContext(), fileURI));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                    Bitmap bmRotated = rotateBitmap(thumbnail, orientation);

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = cameraImage.getLayoutParams().height;

                    int newWidth = (height * bmRotated.getWidth()) / bmRotated.getHeight();
                    cameraImage.getLayoutParams().width = newWidth;
                    cameraImage.getLayoutParams().height = height;

                    cameraImage.setVisibility(View.VISIBLE);
                    playerView.setVisibility(View.GONE);
                    cameraImage.setImageBitmap(bmRotated);
                }
            });
    ActivityResultLauncher<Intent> videoActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    selectMediaType = Constant.VIDEO;
                    cameraImage.setVisibility(View.GONE);
                    playerView.setVisibility(View.VISIBLE);
                    playerView.setFocusable(true);

                    playerView.setPlayer(player);
                    playerView.setUseController(false);
                    MediaItem mediaItem = MediaItem.fromUri(fileURI);
                    player.setMediaItem(mediaItem);
                    player.prepare();
                    player.play();
                }
            });


    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_make_observation);

        Intent myIntent = getIntent();
        stepID = myIntent.getStringExtra(Constant.stepID);
        procedureID = myIntent.getStringExtra(Constant.procedureID);
        menuID = myIntent.getStringExtra(Constant.menuID);
        isFromStepScreen = myIntent.getBooleanExtra(Constant.SCREEN_FROM_STEPS, false);
        if (isFromStepScreen) {
            isFromImageScreen = myIntent.getBooleanExtra(Constant.SCREEN_FROM_STEPS_CAMERA, false);
            stepIndex = myIntent.getIntExtra(Constant.STEP_INDEX, -1);
        }

        playerView = findViewById(R.id.videoView);
        progressBar = findViewById(R.id.progressBar);
        btn_camera = findViewById(R.id.btn_camera);
        btn_next = findViewById(R.id.btn_next);
        spinnerMenu = findViewById(R.id.sp_mainmenu);
        spProcedure = findViewById(R.id.sp_procedure);
        spSteps = findViewById(R.id.sp_steps);
        btn_back = findViewById(R.id.btn_back);
        edt_comment = findViewById(R.id.edt_comment);
        cameraImage = findViewById(R.id.camera_image);
        btn_next.setOnClickListener(v -> {
            Useractivity useractivity = new Useractivity("Make an Obersvation", Constant.userTrackerAction.BUTTON_CLICKED.toString(), "SUBMIT");
            trackUserActivity(useractivity);

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MakeObservationActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
            builder.setTitle("Confirm !").setMessage("Are you sure want to submit task details ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        if (fileURI != null) {
                            progressBar.setVisibility(View.VISIBLE);
                            btn_next.setEnabled(false);
                            UploadImageAsyncTask uploadImageAsyncTask = new UploadImageAsyncTask(getApplicationContext(), this);
                            uploadImageAsyncTask.execute(fileURI);
                        } else {
                            submitTask();
                        }
                    }).setNegativeButton("No", (dialog, which) -> {

                    });
            AlertDialog alert = builder.create();
            alert.show();
        });
        btn_back.setOnClickListener(v -> {
            Useractivity useractivity = new Useractivity("Make an Obersvation", Constant.userTrackerAction.BUTTON_CLICKED.toString(), "BACK");
            trackUserActivity(useractivity);
            finish();
        });
        setupPrioritySpinner();
        getMainMenuList();

        btn_camera.setOnClickListener(v -> {
            Useractivity useractivity = new Useractivity("Make an Obersvation", Constant.userTrackerAction.BUTTON_CLICKED.toString(), "CAMERA");
            trackUserActivity(useractivity);
            if (hasPermissions(getApplicationContext())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MakeObservationActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
                builder.setTitle("Take action !").setMessage("Select Video or Image Capture Options")
                        .setCancelable(false)
                        .setPositiveButton("Image", (dialog, id) -> {

                            takePictureFromCamera();
                        }).setNegativeButton("Cancel", (dialog, which) -> {

                        }).setNeutralButton("Video", (dialog, which) -> {
                            takeVideoFromCamera();
                        });
                AlertDialog alert = builder.create();
                alert.show();

            } else {
                getAllPermissions();
            }


        });

        player = new ExoPlayer.Builder(MakeObservationActivity.this).build();

        Useractivity useractivity = new Useractivity("Make an Obersvation", Constant.userTrackerAction.SCREEN_OPEN.toString());
        trackUserActivity(useractivity);

    }

    private static boolean hasPermissions(Context context) {

        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        String[] PERMISSIONS_13 = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
        String[] STORAGE_PERMISSION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? PERMISSIONS_13 : PERMISSIONS;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && STORAGE_PERMISSION != null) {
            for (String permission : STORAGE_PERMISSION) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void takeVideoFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Camera Video");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        String localfileName = String.valueOf(new Date().getTime());
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "devicevid_" + localfileName + ".mp4");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");

        fileURI = getContentResolver().insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI);
        videoActivityResultLauncher.launch(intent);
    }

    private void takePictureFromCamera() {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.TITLE, "Camera Photo");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        String localfileName = String.valueOf(new Date().getTime());
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "devicepic_" + localfileName + ".jpg");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        values.put(MediaStore.MediaColumns.ORIENTATION, SCREEN_ORIENTATION_LANDSCAPE);
        values.put(MediaStore.Images.ImageColumns.ORIENTATION, SCREEN_ORIENTATION_LANDSCAPE);
        fileURI = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI);
        someActivityResultLauncher.launch(intent);
    }


    private void getAllPermissions() {
        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        String[] PERMISSIONS_13 = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
        String[] STORAGE_PERMISSION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? PERMISSIONS_13 : PERMISSIONS;

        ArrayList<String> permissionsToAskFor = new ArrayList<>();
        for (String permission : STORAGE_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToAskFor.add(permission);
            }
        }
        if (!permissionsToAskFor.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToAskFor.toArray(new String[0]), 1);
        }
    }

    private void setupMainmenuSpinner() {
        spinnerMenu.setVisibility(View.VISIBLE);
        adapterMainmenu = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, mainmenuList);
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
        adapterProcedure =
                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, procedureList);
        adapterProcedure.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spProcedure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    getStespList(procedureList.get(position).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spProcedure.setAdapter(adapterProcedure);
    }

    private void setupStepsSpinner() {
        spSteps.setVisibility(View.VISIBLE);
        adapterSteps =
                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, stepsList);
        adapterSteps.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSteps.setAdapter(adapterSteps);
    }

    private void setupPrioritySpinner() {
        spPriority = findViewById(R.id.sp_priority);
        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, priority);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(ad);
        spPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    Useractivity useractivity = new Useractivity("Make an Obersvation", Constant.userTrackerAction.PRIORITY.toString(), priority[spPriority.getSelectedItemPosition()]);
                    trackUserActivity(useractivity);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

                        runOnUiThread(() -> {
                            for (int position = 0; position < mainmenuList.size(); position++) {
                                MainMenuResponse value = mainmenuList.get(position);
                                if (value.getId() != null && value.getId().equals(menuID)) {
                                    spinnerMenu.setSelection(position);
                                    menuID = "";
                                    return;
                                }
                            }
                        });

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

                        runOnUiThread(() -> {
                            for (int position = 0; position < procedureList.size(); position++) {
                                ProcedureResponse value = procedureList.get(position);
                                if (value.getId() != null && value.getId().equals(procedureID)) {
                                    spProcedure.setSelection(position);
                                    procedureID = "";
                                    return;
                                }
                            }
                        });
                    } else {
                        spProcedure.setVisibility(View.GONE);
                        spSteps.setVisibility(View.GONE);
                        procedureList.clear();
                        stepsList.clear();
                        if (adapterProcedure != null)
                            adapterProcedure.notifyDataSetChanged();
                        if (adapterSteps != null)
                            adapterSteps.notifyDataSetChanged();


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


                        runOnUiThread(() -> {
                            for (int position = 0; position < stepsList.size(); position++) {
                                StepsResponse value = stepsList.get(position);
                                if (value.getId() != null && value.getId().equals(stepID)) {
                                    spSteps.setSelection(position);
                                    stepID = "";
                                    return;
                                }
                            }
                        });

                    } else {
                        spSteps.setVisibility(View.GONE);
                        stepsList.clear();
                        if (adapterSteps != null)
                            adapterSteps.notifyDataSetChanged();
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
        progressBar.setVisibility(View.VISIBLE);

        SurveyRequest surveyRequest = new SurveyRequest();

        if (spinnerMenu.getSelectedItemPosition() > 0) {
            surveyRequest.setMenuId(mainmenuList.get(spinnerMenu.getSelectedItemPosition()).getId());
        }
        if (spProcedure.getSelectedItemPosition() > 0 && procedureList.size() > 0) {
            surveyRequest.setProcedureId(procedureList.get(spProcedure.getSelectedItemPosition()).getId());
        }
        if (spSteps.getSelectedItemPosition() > 0 && stepsList.size() > 0) {
            surveyRequest.setStepId(stepsList.get(spSteps.getSelectedItemPosition()).getId());
        }


        if (spPriority.getSelectedItemPosition() > 0) {
            surveyRequest.setPriority(priority[spPriority.getSelectedItemPosition()]);
        }
        if (!TextUtils.isEmpty(selectedMedia)) {
            surveyRequest.setMedia(selectedMedia);
            surveyRequest.setMediaType(selectMediaType);
        }
        surveyRequest.setText(edt_comment.getText().toString());

        btn_next.setEnabled(false);

        ApiClient.createSurvey(getApplicationContext(), false, surveyRequest).enqueue(new Callback<SurveyResponse>() {
            @Override
            public void onResponse(Call<SurveyResponse> call, Response<SurveyResponse> response) {
                progressBar.setVisibility(View.GONE);
                btn_next.setEnabled(true);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        edt_comment.setText("");
                        selectedMedia = "";
                        cameraImage.setVisibility(View.GONE);
                        fileURI = null;
                        Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_SHORT).show();
                        if (isFromStepScreen && stepIndex >= 0) {
                            if (isFromImageScreen) {
                                globlestepsList.get(stepIndex).setMediaSubmitted(true);
                            } else {
                                globlestepsList.get(stepIndex).setTextSubmitted(true);
                            }
                        }

                        finish();
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
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSuccess(String filename) {
        selectedMedia = filename;
        progressBar.setVisibility(View.GONE);
        btn_next.setEnabled(true);
        submitTask();
    }

    @Override
    public void onFailer() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Image Upload fails", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) player.release();
    }
}