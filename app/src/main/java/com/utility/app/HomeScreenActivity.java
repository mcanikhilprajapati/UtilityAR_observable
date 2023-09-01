package com.utility.app;

import static com.utility.app.Constant.APPLICATION_ID;
import static com.utility.app.Constant.CHAT_BACK_TO_VIDEO;
import static com.utility.app.Constant.CHAT_IMAGE;
import static com.utility.app.Constant.CLEAR_DRAWING;
import static com.utility.app.Constant.SDK_NAME;
import static com.utility.app.Constant.STORAGE_CONTAINER;
import static com.utility.app.Constant.STROKE_UPDATE;
import static com.utility.app.Constant.connectionString;
import static com.utility.app.Constant.endpoint;
import static com.utility.app.Constant.imagePath;
import static com.utility.app.Constant.sdkVersion;
import static com.utility.app.FileUtils.getFile;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.azure.android.communication.calling.AcceptCallOptions;
import com.azure.android.communication.calling.Call;
import com.azure.android.communication.calling.CallAgent;
import com.azure.android.communication.calling.CallClient;
import com.azure.android.communication.calling.CallState;
import com.azure.android.communication.calling.CallingCommunicationException;
import com.azure.android.communication.calling.CreateViewOptions;
import com.azure.android.communication.calling.DeviceManager;
import com.azure.android.communication.calling.GroupCallLocator;
import com.azure.android.communication.calling.IncomingCall;
import com.azure.android.communication.calling.JoinCallOptions;
import com.azure.android.communication.calling.LocalVideoStream;
import com.azure.android.communication.calling.ParticipantsUpdatedEvent;
import com.azure.android.communication.calling.ParticipantsUpdatedListener;
import com.azure.android.communication.calling.PropertyChangedEvent;
import com.azure.android.communication.calling.PropertyChangedListener;
import com.azure.android.communication.calling.RemoteParticipant;
import com.azure.android.communication.calling.ScalingMode;
import com.azure.android.communication.calling.VideoDeviceInfo;
import com.azure.android.communication.calling.VideoOptions;
import com.azure.android.communication.calling.VideoStreamRenderer;
import com.azure.android.communication.calling.VideoStreamRendererView;
import com.azure.android.communication.chat.ChatAsyncClient;
import com.azure.android.communication.chat.ChatClientBuilder;
import com.azure.android.communication.chat.ChatThreadAsyncClient;
import com.azure.android.communication.chat.ChatThreadClientBuilder;
import com.azure.android.communication.chat.models.ChatMessageType;
import com.azure.android.communication.chat.models.ChatParticipant;
import com.azure.android.communication.chat.models.CreateChatThreadOptions;
import com.azure.android.communication.chat.models.SendChatMessageOptions;
import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.common.CommunicationUserIdentifier;
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier;
import com.azure.android.communication.common.PhoneNumberIdentifier;
import com.azure.android.communication.common.UnknownIdentifier;
import com.azure.android.core.http.policy.UserAgentPolicy;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.utilityar.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

//https://learn.microsoft.com/en-us/azure/communication-services/quickstarts/chat/get-started?tabs=windows&pivots=programming-language-android
//for calling https://learn.microsoft.com/en-us/azure/communication-services/quickstarts/voice-video-calling/get-started-with-video-calling?pivots=platform-android

public class HomeScreenActivity extends AppCompatActivity {

    private CallAgent callAgent;
    private VideoDeviceInfo currentCamera;
    private LocalVideoStream currentVideoStream;
    private DeviceManager deviceManager;
    private IncomingCall incomingCall;
    private Call call;
    VideoStreamRenderer previewRenderer;
    VideoStreamRendererView preview;
    private ParticipantsUpdatedListener remoteParticipantUpdatedListener;
    private PropertyChangedListener onStateChangedListener;

    final HashSet<String> joinedParticipants = new HashSet<>();

    Button switchSourceButton;

    private String userToken = "";
    private String communicationUserId = "";

    private String clientID = "";
    private String chatThreadId = "";
    private String email = "";

    private DrawablePathImage cameraImage;
    private TextView txt_username;
    private final int cameraRequestCode = 1337;
    private Uri imageUri;
    private Button takePicture, btnBackToVideo, btlCallStart, btnLogout, btnEndCall, btnMute;

    private ChatAsyncClient chatAsyncClient;
    private ChatThreadAsyncClient chatThreadAsyncClient;

    private String fileName = "";
    private final Handler ha = new Handler();
    private Runnable runnable;
    private ProgressBar pbar, pbarCall;

    ArrayList<String> paths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //get user details from local storage
        userToken = SessionManager.pref.getString(SessionManager.TOKEN, "");
        communicationUserId = SessionManager.pref.getString(SessionManager.COMMUNICATIONUSERID, "");
        clientID = SessionManager.pref.getString(SessionManager.GROUPCALL_ID, "");
        chatThreadId = SessionManager.pref.getString(SessionManager.CHATTHREAD_ID, "");
        email = SessionManager.pref.getString(SessionManager.EMAIL, "");


        getAllPermissions();
        createAgent();
        handleIncomingCall();

        initUI();

        txt_username.setText(email);
        cameraImage.setVisibility(View.INVISIBLE);
        switchSourceButton.setOnClickListener(l -> switchSource());


        btnMute.setOnClickListener(v -> {
            if (call.isMuted()) {
                final Drawable drawableTop = getResources().getDrawable(R.drawable.ic_mic);

                call.unmute(getApplicationContext());
                btnMute.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
                btnMute.setText("Mute");
            } else {
                final Drawable drawableTop = getResources().getDrawable(R.drawable.ic_mute);

                call.mute(getApplicationContext());
                btnMute.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
                btnMute.setText("Unmute");
            }
        });

//        call.addOnIsMutedChangedListener(new PropertyChangedListener() {
//            @Override
//            public void onPropertyChanged(PropertyChangedEvent propertyChangedEvent) {
//
//            }
//        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (call != null) {
                    hangUp();

                    if (runnable != null)
                        ha.removeCallbacks(runnable);
                }

                SessionManager.getInstance().logout();
                Intent intent = new Intent(HomeScreenActivity.this, LoginScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnBackToVideo.setOnClickListener(v -> {


            if (runnable != null)
                ha.removeCallbacks(runnable);


            takePicture.setVisibility(View.VISIBLE);
            cameraImage.setVisibility(View.GONE);
            btnBackToVideo.setVisibility(View.GONE);

            turnOnLocalVideo();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("event", CHAT_BACK_TO_VIDEO);
                //send Message to chat
                senEventToWeb(jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


            paths.clear();
            cameraImage.clearPath();
        });

        takePicture.setOnClickListener(v -> {
            if (runnable != null)
                ha.removeCallbacks(runnable);

            turnOffLocalVideo();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Camera Photo");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            String localfileName = String.valueOf(new Date().getTime());
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "devicepic_" + localfileName + ".jpg");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);


            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, cameraRequestCode);
            //https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative

        });


        btlCallStart.setOnClickListener(v -> {

            startCall();
//            turnOffLocalVideo();
//            turnOnLocalVideo();
            btlCallStart.setVisibility(View.GONE);
            btnEndCall.setVisibility(View.VISIBLE);
            pbarCall.setVisibility(View.VISIBLE);
        });
        btnEndCall.setOnClickListener(v -> {
            hangUp();
            cameraImage.setVisibility(View.GONE);
            btlCallStart.setVisibility(View.VISIBLE);
            btnEndCall.setVisibility(View.GONE);
            btnBackToVideo.setVisibility(View.GONE);
            pbarCall.setVisibility(View.GONE);
        });

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        switchSourceButton.setVisibility(View.GONE);
    }

    private void initUI() {
        btnMute = findViewById(R.id.btn_mute);
        txt_username = findViewById(R.id.txt_username);
        btnLogout = findViewById(R.id.btn_exit);
        cameraImage = findViewById(R.id.camera_image);
        pbar = findViewById(R.id.pbar);
        pbarCall = findViewById(R.id.pbar_call);
        btlCallStart = findViewById(R.id.call_button);
        btnEndCall = findViewById(R.id.callend_button);
        takePicture = findViewById(R.id.get_image1);
        btnBackToVideo = findViewById(R.id.backto_video);
        switchSourceButton = findViewById(R.id.switch_source);
    }


    private void senEventToWeb(JSONObject jsonObject) {

        try {
            final String content = jsonObject.toString();
            SendChatMessageOptions chatMessageOptions = new SendChatMessageOptions()
                    .setType(ChatMessageType.TEXT)
                    .setContent(content);
            if (chatThreadAsyncClient != null)
                chatThreadAsyncClient.sendMessage(chatMessageOptions).get().getId();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    public void initSDk() {
        try {
            AndroidThreeTen.init(this);

            // <CREATE A CHAT CLIENT>
            chatAsyncClient = new ChatClientBuilder()
                    .endpoint(endpoint)
                    .credential(new CommunicationTokenCredential(userToken))
                    .addPolicy(new UserAgentPolicy(APPLICATION_ID, SDK_NAME, sdkVersion))
                    .buildAsyncClient();

            // <CREATE A CHAT THREAD>
            // A list of ChatParticipant to start the thread with.
            List<ChatParticipant> participants = new ArrayList<>();
            // The display name for the thread participant.
            String displayName = "User";
            participants.add(new ChatParticipant()
                    .setCommunicationIdentifier(new CommunicationUserIdentifier(communicationUserId))
                    .setDisplayName(displayName));

            // The topic for the thread.
            final String topic = "Utility AR";
            // Optional, set a repeat request ID.
            final String repeatabilityRequestID = "";
            // Options to pass to the create method.
            CreateChatThreadOptions createChatThreadOptions = new CreateChatThreadOptions()
                    .setTopic(topic)
                    .setParticipants(participants)
                    .setIdempotencyToken(repeatabilityRequestID);

            chatAsyncClient.createChatThread(createChatThreadOptions).get();

            // <CREATE A CHAT THREAD CLIENT>
            chatThreadAsyncClient = new ChatThreadClientBuilder()
                    .endpoint(endpoint)
                    .credential(new CommunicationTokenCredential(userToken))
                    .addPolicy(new UserAgentPolicy(APPLICATION_ID, SDK_NAME, sdkVersion))
                    .chatThreadId(chatThreadId)
                    .buildAsyncClient();


            // <RECEIVE CHAT MESSAGES>
            // Start real time notification
//            chatAsyncClient.startRealtimeNotifications(getApplicationContext(), new Consumer<Throwable>() {
//                @Override
//                public void accept(Throwable throwable) {
//                    System.out.println("115 =3378" + throwable.getMessage());
//                }
//            });


            // Register a listener for chatMessageReceived event
//            chatAsyncClient.addEventHandler(ChatEventType.CHAT_MESSAGE_RECEIVED, (ChatEvent payload) -> {
//                ChatMessageReceivedEvent chatMessageReceivedEvent = (ChatMessageReceivedEvent) payload;
//                // You code to handle chatMessageReceived event
//                System.out.println("115 =?" + chatMessageReceivedEvent.getContent());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), chatMessageReceivedEvent.getContent(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            });

            //<MESSAGE POOLING EVERY 3 Seconds>
            poolMessages();


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private void poolMessages() {
        runnable = new Runnable() {
            @Override
            public void run() {
                chatThreadAsyncClient.listMessages().forEach(message -> {

                    try {
                        JSONObject jsonObject = new JSONObject(message.getContent().getMessage());
                        String eventType = jsonObject.getString("event");
                        if (eventType.equalsIgnoreCase(STROKE_UPDATE)) {
                            //DRAW PATH ON IMAGE
                            JSONObject data = jsonObject.getJSONObject("data");
                            runOnUiThread(() -> {
                                try {
                                    paths.add(data.getString("paths"));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                cameraImage.setPathToDraw(paths);

                            });
                        } else if (eventType.equalsIgnoreCase(CLEAR_DRAWING)) {
                            //CLEAR PATH FROM IMAGE
                            paths.clear();
                            cameraImage.clearPath();
                        }
                        //<DELETE EACH MESSAGE ON RECEIVED>
                        if (chatThreadAsyncClient != null)
                            chatThreadAsyncClient.deleteMessage(message.getId()).get();
                    } catch (JSONException | InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }

                });

                ha.postDelayed(this, 1000);
            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case cameraRequestCode:
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

                        int newWidth = (height * thumbnail.getWidth()) / thumbnail.getHeight();
                        cameraImage.getLayoutParams().width = newWidth;
                        cameraImage.getLayoutParams().height = height;
                        cameraImage.setImageBitmap(thumbnail);

                        cameraImage.setVisibility(View.VISIBLE);
                        btnBackToVideo.setVisibility(View.VISIBLE);

                        //<UPLOAD IMAGE TO AZURE  STORAGE AND SEND EVENT WITH URL>
                        new UploadImageAsyncTask().execute(imageUri);


                    }
                    break;
            }

        } else {
            turnOnLocalVideo();
            cameraImage.setVisibility(View.GONE);
            btnBackToVideo.setVisibility(View.GONE);
        }
    }


    private void createEventForImageUploadSuccess() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("event", CHAT_IMAGE);
            jsonObject.put("data", imagePath + fileName);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (Exception x) {
        }

        //send message to chat
        senEventToWeb(jsonObject);
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

    //<SETUP NEW CALL AGENT>
    private void createAgent() {
        Context context = this.getApplicationContext();

        try {
            CommunicationTokenCredential credential = new CommunicationTokenCredential(userToken);
            CallClient callClient = new CallClient();
            deviceManager = callClient.getDeviceManager(context).get();
            callAgent = callClient.createCallAgent(getApplicationContext(), credential).get();

        } catch (Exception ex) {

            System.out.println(ex.getMessage());
            Toast.makeText(context, "Failed to create call agent.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleIncomingCall() {
        callAgent.addOnIncomingCallListener((incomingCall) -> {
            this.incomingCall = incomingCall;
            Executors.newCachedThreadPool().submit(this::answerIncomingCall);
        });
    }

    //<START NEW GROUP CALL>
    private void startCall() {
        Context context = this.getApplicationContext();
        List<VideoDeviceInfo> cameras = deviceManager.getCameras();


        JoinCallOptions options = new JoinCallOptions();
        if (!cameras.isEmpty()) {
            currentCamera = getNextAvailableCamera(null);
            currentVideoStream = new LocalVideoStream(currentCamera, context);
            LocalVideoStream[] videoStreams = new LocalVideoStream[1];
            videoStreams[0] = currentVideoStream;
            VideoOptions videoOptions = new VideoOptions(videoStreams);
            options.setVideoOptions(videoOptions);
            showPreview(currentVideoStream);
        }
        UUID t = UUID.fromString(clientID);

        GroupCallLocator groupCallLocator = new GroupCallLocator(t);

        call = callAgent.join(
                context,
                groupCallLocator,
                options);


        //<INIT CHAT FOR EVENTS>
        initSDk();

        remoteParticipantUpdatedListener = this::handleRemoteParticipantsUpdate;
        onStateChangedListener = this::handleCallOnStateChanged;
        call.addOnRemoteParticipantsUpdatedListener(remoteParticipantUpdatedListener);
        call.addOnStateChangedListener(onStateChangedListener);

    }

    //<END CALL>
    private void hangUp() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                try {if(call!=null) {
                    call.hangUp().get();
                    btnMute.setVisibility(View.GONE);
                }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                if (previewRenderer != null) {
                    previewRenderer.dispose();
                }

            }
        });

    }

    public void turnOnLocalVideo() {
        List<VideoDeviceInfo> cameras = deviceManager.getCameras();
        if (!cameras.isEmpty()) {
            try {
                currentVideoStream = new LocalVideoStream(currentCamera, this);
                showPreview(currentVideoStream);
                call.startVideo(this, currentVideoStream).get();

            } catch (CallingCommunicationException acsException) {
                acsException.printStackTrace();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            } catch (Exception exception) {
            }
        }
    }

    public void turnOffLocalVideo() {
        try {
            RelativeLayout container = findViewById(R.id.localvideocontainer);
            for (int i = 0; i < container.getChildCount(); ++i) {
                Object tag = container.getChildAt(i).getTag();
                if (tag != null && (int) tag == 0) {
                    container.removeViewAt(i);
                }
            }
            switchSourceButton.setVisibility(View.GONE);
            previewRenderer.dispose();
            previewRenderer = null;
            call.stopVideo(this, currentVideoStream).get();
        } catch (CallingCommunicationException acsException) {
            acsException.printStackTrace();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception exception) {
        }
    }

    private VideoDeviceInfo getNextAvailableCamera(VideoDeviceInfo camera) {
        List<VideoDeviceInfo> cameras = deviceManager.getCameras();
        int currentIndex = 0;
        if (camera == null) {
            return cameras.isEmpty() ? null : cameras.get(0);
        }

        for (int i = 0; i < cameras.size(); i++) {
            if (camera.getId().equals(cameras.get(i).getId())) {
                currentIndex = i;
                break;
            }
        }
        int newIndex = (currentIndex + 1) % cameras.size();

        return cameras.get(newIndex);
    }

    private void showPreview(LocalVideoStream stream) {

        previewRenderer = new VideoStreamRenderer(stream, this);
        RelativeLayout layout = findViewById(R.id.localvideocontainer);
        preview = previewRenderer.createView(new CreateViewOptions(ScalingMode.CROP));
        preview.setTag(0);
        runOnUiThread(() -> {
            layout.addView(preview);

        });
    }

    //<SWITCH CAMERA>
    public void switchSource() {
        if (currentVideoStream != null) {
            try {
                currentCamera = getNextAvailableCamera(currentCamera);
                currentVideoStream.switchSource(currentCamera).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleCallOnStateChanged(PropertyChangedEvent args) {

        if (call.getState() == CallState.CONNECTED) {
            runOnUiThread(() -> {
                takePicture.setVisibility(View.VISIBLE);
                switchSourceButton.setVisibility(View.VISIBLE);
                btnMute.setVisibility(View.VISIBLE);
                pbarCall.setVisibility(View.GONE);
            });
            handleCallState();
        }
        if (call.getState() == CallState.DISCONNECTED) {
            runOnUiThread(() -> {
                takePicture.setVisibility(View.GONE);
                switchSourceButton.setVisibility(View.GONE);
                btnMute.setVisibility(View.GONE);
                if (previewRenderer != null) {
                    previewRenderer.dispose();
                }

            });


        }

        runOnUiThread(() -> Toast.makeText(this, call.getState().toString(), Toast.LENGTH_SHORT).show());
    }

    private void handleCallState() {
        handleAddedParticipants(call.getRemoteParticipants());
    }

    private void answerIncomingCall() {
        Context context = this.getApplicationContext();
        if (incomingCall == null) {
            return;
        }
        AcceptCallOptions acceptCallOptions = new AcceptCallOptions();
        List<VideoDeviceInfo> cameras = deviceManager.getCameras();
        if (!cameras.isEmpty()) {
            currentCamera = getNextAvailableCamera(null);
            currentVideoStream = new LocalVideoStream(currentCamera, context);
            LocalVideoStream[] videoStreams = new LocalVideoStream[1];
            videoStreams[0] = currentVideoStream;
            VideoOptions videoOptions = new VideoOptions(videoStreams);
            acceptCallOptions.setVideoOptions(videoOptions);
            showPreview(currentVideoStream);
        }
        try {
            call = incomingCall.accept(context, acceptCallOptions).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        remoteParticipantUpdatedListener = this::handleRemoteParticipantsUpdate;
        onStateChangedListener = this::handleCallOnStateChanged;
        call.addOnRemoteParticipantsUpdatedListener(remoteParticipantUpdatedListener);
        call.addOnStateChangedListener(onStateChangedListener);
    }

    public void handleRemoteParticipantsUpdate(ParticipantsUpdatedEvent args) {
        handleAddedParticipants(args.getAddedParticipants());
        handleRemovedParticipants(args.getRemovedParticipants());

    }

    private void handleAddedParticipants(List<RemoteParticipant> participants) {

        for (RemoteParticipant remoteParticipant : participants) {

            if (!joinedParticipants.contains(getId(remoteParticipant))) {

                joinedParticipants.add(getId(remoteParticipant));
                ChatParticipant participant = new ChatParticipant()
                        .setCommunicationIdentifier(new CommunicationUserIdentifier(remoteParticipant.getIdentifier().getRawId()))
                        .setDisplayName(remoteParticipant.getDisplayName());

                try {
                    chatThreadAsyncClient.addParticipant(participant).get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public String getId(final RemoteParticipant remoteParticipant) {
        final CommunicationIdentifier identifier = remoteParticipant.getIdentifier();
        if (identifier instanceof PhoneNumberIdentifier) {
            return ((PhoneNumberIdentifier) identifier).getPhoneNumber();
        } else if (identifier instanceof MicrosoftTeamsUserIdentifier) {
            return ((MicrosoftTeamsUserIdentifier) identifier).getUserId();
        } else if (identifier instanceof CommunicationUserIdentifier) {
            return ((CommunicationUserIdentifier) identifier).getId();
        } else {
            return ((UnknownIdentifier) identifier).getId();
        }
    }

    private void handleRemovedParticipants(List<RemoteParticipant> removedParticipants) {
        for (RemoteParticipant remoteParticipant : removedParticipants) {

            if (joinedParticipants.contains(getId(remoteParticipant))) {
                joinedParticipants.remove(getId(remoteParticipant));
                try {
                    chatThreadAsyncClient.removeParticipant(remoteParticipant.getIdentifier()).get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (call != null) {
            hangUp();
        }
        if (runnable != null)
            ha.removeCallbacks(runnable);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (callAgent != null) {
            callAgent.dispose();
        }
    }

    //<UPLOAD IMAGE>
    private class UploadImageAsyncTask extends AsyncTask<Uri, Integer, Object> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "Sending Image", Toast.LENGTH_SHORT).show();
                pbar.setVisibility(View.VISIBLE);
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
                    fileName = "image_" + new Date().getTime() + ".jpg";

                    // Create or overwrite the blob with contents from a local file
                    CloudBlockBlob blob = container.getBlockBlobReference(fileName);

                    File imagefile = getFile(getApplicationContext(), urls[0]);

                    blob.upload(new FileInputStream(imagefile), imagefile.length());

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
                pbar.setVisibility(View.GONE);
                createEventForImageUploadSuccess();
                ha.postDelayed(runnable, 3000);
            });
        }
    }
}
