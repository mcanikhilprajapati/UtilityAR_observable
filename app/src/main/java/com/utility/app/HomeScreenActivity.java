package com.utility.app;

import static com.utility.app.Constant.STORAGE_CONTAINER;
import static com.utility.app.Constant.connectionString;
import static com.utility.app.FileUtils.getFile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.utility.app.adapters.procedureAdapter;
import com.utility.app.listener.OnItemClickListener;
import com.utility.app.models.Observations;
import com.utilityar.app.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//TODO https://www.digitalocean.com/community/tutorials/android-recyclerview-load-more-endless-scrolling

public class HomeScreenActivity extends AppCompatActivity {


    Uri imageUri = null;
    private ArrayList<Observations> courseArrayList;
    private RecyclerView courseRV;
    private procedureAdapter procedureAdapter;
    private Button btn_make_observation,btnChangeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


//        getAllPermissions();
        initUI();
// initializing our array list.
        courseArrayList = new ArrayList<>();
        courseArrayList.add(new Observations("Main Board 1"));
        courseArrayList.add(new Observations("Boiler 2"));
        courseArrayList.add(new Observations("Risk Assessment"));
        courseArrayList.add(new Observations("PPE Checker"));
        courseArrayList.add(new Observations("Surveying"));
        courseArrayList.add(new Observations("Pump1"));

        // on below line we are adding our array list to our adapter class.
        procedureAdapter = new procedureAdapter(this, courseArrayList, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getApplicationContext(), ProcedureScreenActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        courseRV.setLayoutManager(manager);
        // on below line we are setting
        // adapter to our recycler view.
        courseRV.setAdapter(procedureAdapter);
//        takePicture.setOnClickListener(v -> {
//
//            ContentValues values = new ContentValues();
//            values.put(MediaStore.Images.Media.TITLE, "Camera Photo");
//            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
//            String localfileName = String.valueOf(new Date().getTime());
//            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "devicepic_" + localfileName + ".jpg");
//            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
//            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
//
//
//            imageUri = getContentResolver().insert(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//            startActivityForResult(intent, 100);
//            //https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative
//
//        });


    }

    private void initUI() {
        // initializing our variables.
        btnChangeUser = findViewById(R.id.btn_change_user);
        btn_make_observation = findViewById(R.id.btn_make_observation);
        courseRV = findViewById(R.id.idRVCourses);

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
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 100:
                    if (resultCode == RESULT_OK) {
                        Bitmap thumbnail = null;

                        try {
                            thumbnail = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        int height = displayMetrics.heightPixels;

//                        int newWidth = (height * thumbnail.getWidth()) / thumbnail.getHeight();
//                        cameraImage.getLayoutParams().width = newWidth;
//                        cameraImage.getLayoutParams().height = height;
//                        cameraImage.setImageBitmap(thumbnail);


                        //<UPLOAD IMAGE TO AZURE  STORAGE AND SEND EVENT WITH URL>
                        new UploadImageAsyncTask().execute(imageUri);


                    }
                    break;
            }

        } else {
            //TODO
        }
    }


    private void getAllPermissions() {
        String[] requiredPermissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION};
        ArrayList<String> permissionsToAskFor = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToAskFor.add(permission);
            }
        }
        if (!permissionsToAskFor.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToAskFor.toArray(new String[0]), 1);
        }
    }


    //TODO
    // https://github.com/Azure/azure-storage-android/blob/master/microsoft-azure-storage-samples/src/com/microsoft/azure/storage/samples/blob/BlobGettingStartedTask.java
    //https://learn.microsoft.com/en-us/java/api/overview/azure/storage?view=azure-java-stable

    //https://learn.microsoft.com/en-us/azure/storage/blobs/storage-quickstart-blobs-java?tabs=powershell%2Cmanaged-identity%2Croles-azure-portal%2Csign-in-azure-cli


    //<UPLOAD IMAGE>
    private class UploadImageAsyncTask extends AsyncTask<Uri, Integer, Object> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "Sending Image", Toast.LENGTH_SHORT).show();
//                pbar.setVisibility(View.VISIBLE);
            });
        }

        protected Object doInBackground(Uri... urls) {
            try {

                try {
                    // Retrieve storage account from connection-string
                    CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);

                    // Create the blob client
                    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                    // Get a reference to a container
                    // The container name must be lower case
                    CloudBlobContainer container = blobClient.getContainerReference(STORAGE_CONTAINER);

                    // Create the container if it does not exist
                    container.createIfNotExists();

                    // Create a permissions object
                    BlobContainerPermissions containerPermissions = new BlobContainerPermissions();

                    // Include public access in the permissions object
                    containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

                    // Set the permissions on the container
                    container.uploadPermissions(containerPermissions);


                    //File name that store on storage
//                    fileName = "image_" + new Date().getTime() + ".jpg";

                    // Create or overwrite the blob with contents from a local file
//                    CloudBlockBlob blob = container.getBlockBlobReference(fileName);

                    File imagefile = getFile(getApplicationContext(), urls[0]);

//                    blob.upload(new FileInputStream(imagefile), imagefile.length());

                } catch (Exception e) {
                    System.out.println(853 + e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(857 + e.getMessage());
            }
            return null;
        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
//                pbar.setVisibility(View.GONE);
//                createEventForImageUploadSuccess();
//                ha.postDelayed(runnable, 3000);
            });
        }
    }
}
