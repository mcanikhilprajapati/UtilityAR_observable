package com.utility.app;

import static com.utility.app.Constant.STORAGE_CONTAINER;
import static com.utility.app.Constant.connectionString;
import static com.utility.app.FileUtils.getFile;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.utility.app.listener.OnFileUploadListner;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;


public class UploadImageAsyncTask extends AsyncTask<Uri, Integer, Object> {
    private Context contextRef;
    private OnFileUploadListner onFileUploadListner;
    private String fileName = "";

    public UploadImageAsyncTask(Context context, OnFileUploadListner onFileUploadListner) {
        contextRef = context;
        this.onFileUploadListner = onFileUploadListner;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        Toast.makeText(contextRef, "Sending Image", Toast.LENGTH_SHORT).show();
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
//                fileName = "survey_image_" + new Date().getTime() + ".jpg";
                fileName = "survey_vid_" + new Date().getTime() + ".mp4";

                // Create or overwrite the blob with contents from a local file
                CloudBlockBlob blob = container.getBlockBlobReference(fileName);

                File imagefile = getFile(contextRef, urls[0]);

                blob.upload(new FileInputStream(imagefile), imagefile.length());

            } catch (Exception e) {
                System.out.println(853 + e.getMessage());
            }
        } catch (Exception e) {
            onFileUploadListner.onFailer();
            e.printStackTrace();
            System.out.println(857 + e.getMessage());
        }
        return null;
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        onFileUploadListner.onSuccess(fileName);
//        runOnUiThread(() -> {
//            Toast.makeText(contextRef, "Image Uploaded", Toast.LENGTH_SHORT).show();
//            pbar.setVisibility(View.GONE);
//            createEventForImageUploadSuccess();
//            ha.postDelayed(runnable, 3000);
//        });
    }
}