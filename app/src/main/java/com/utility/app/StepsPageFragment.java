package com.utility.app;

import static com.utility.app.StepsDetailsScreenActivity.globlestepsList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.utility.app.listener.OnViewPagerClickListener;
import com.utility.app.models.StepsResponse;
import com.utility.app.models.SurveyResponse;
import com.utility.app.models.request.SurveyRequest;
import com.utility.app.models.request.Useractivity;
import com.utility.app.retrofit.ApiClient;
import com.utilityar.app.R;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StepsPageFragment extends Fragment {
    private OnViewPagerClickListener onViewPagerClickListener;
    StepsResponse stepsResponse;
    private PlayerView playerView;
    ExoPlayer player;
    private Button btn_next, btn_back, btn_home, btnTextAdd, btn_camera, btn_input_type;
    private TextView txt_step_name, txt_description;
    private AppCompatImageView img_task_image;
    private WebView webview;
    private ScrollView sc_txt_description;
    private Context context;
    private boolean isLast = false;
    private int position = 0;
    private String menuName = "";
    private String procedureName = "";
    public StepsPageFragment() {

    }

    public StepsPageFragment(StepsResponse stepsResponse, int position, boolean isLast, String menuName,String procedureName,OnViewPagerClickListener onViewPagerClickListener) {
        this.stepsResponse = stepsResponse;
        this.isLast = isLast;
        this.position = position;
        this.menuName = menuName;
        this.procedureName = procedureName;
        this.onViewPagerClickListener = onViewPagerClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(
                R.layout.row_item_video_viewpager_steps, container, false);
        context = itemView.getContext();
        player = new ExoPlayer.Builder(context).build();
        playerView = itemView.findViewById(R.id.videoView);

        txt_step_name = itemView.findViewById(R.id.txt_toolbar_title);
        txt_description = itemView.findViewById(R.id.txt_description);
        btn_next = itemView.findViewById(R.id.btn_next);
        btn_back = itemView.findViewById(R.id.btn_back);
        btn_home = itemView.findViewById(R.id.btn_home);
        img_task_image = itemView.findViewById(R.id.img_task_image);
        btnTextAdd = itemView.findViewById(R.id.btnTextAdd);
        btn_camera = itemView.findViewById(R.id.btn_camera);
        btn_input_type = itemView.findViewById(R.id.btn_input_type);
        webview = itemView.findViewById(R.id.webview);
        sc_txt_description = itemView.findViewById(R.id.sc_txt_description);
        return itemView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (stepsResponse.getMediaType().equals("VIDEO")) {
            playerView.setVisibility(View.VISIBLE);
            playerView.setPlayer(player);
            player.setRepeatMode(Player.REPEAT_MODE_ONE);
            playerView.setUseController(false);
            MediaItem mediaItem = MediaItem.fromUri(stepsResponse.getMedia());
            player.setMediaItem(mediaItem);
            player.prepare();
        } else {
            playerView.setVisibility(View.GONE);
        }


        txt_step_name.setText(stepsResponse.getName());
        if (!TextUtils.isEmpty(stepsResponse.getDescription())) {
            txt_description.setText(stepsResponse.getDescription());
            txt_description.setVisibility(View.VISIBLE);
            sc_txt_description.setVisibility(View.VISIBLE);
        } else {
            txt_description.setVisibility(View.GONE);
            sc_txt_description.setVisibility(View.GONE);
        }

        if (isLast) {
            btn_next.setText("Finish");
        } else {
            btn_next.setText("Next");
        }

        img_task_image.setVisibility(View.GONE);
        webview.setVisibility(View.GONE);


        if (stepsResponse.getMediaType().equals("URL")) {
            webview.setVisibility(View.VISIBLE);
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webview.loadUrl(stepsResponse.getMedia());
            webview.setWebChromeClient(new WebChromeClient());

        }
        if (stepsResponse.getMediaType().equals("IMAGE")) {
            img_task_image.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .asBitmap()      //get hieght and width
                    .load(stepsResponse.getMedia())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap thumbnail, @Nullable
                        Transition<? super Bitmap> transition) {

                            try {
                                int height = img_task_image.getLayoutParams().height;
                                int newWidth = (height * thumbnail.getWidth()) / thumbnail.getHeight();

                                img_task_image.getLayoutParams().width = newWidth;
                                img_task_image.getLayoutParams().height = height;
                                img_task_image.setImageBitmap(thumbnail);
                            } catch (Exception exception) {
                            }
                        }
                    });
        }

        btnTextAdd.setOnClickListener(v -> {
            if (onViewPagerClickListener != null) {
                onViewPagerClickListener.onButtonClick(position, false);
            }
        });

        btn_back.setOnClickListener(v -> {
            if (onViewPagerClickListener != null) {
                onViewPagerClickListener.onBackClick(position);
            }
        });
        btn_next.setOnClickListener(v -> {
            if (onViewPagerClickListener != null) {
                onViewPagerClickListener.onNextClick(position);
            }
        });
        btn_home.setOnClickListener(v -> {
            if (onViewPagerClickListener != null) {
                onViewPagerClickListener.onHomeClick(position);
            }
        });


        btn_camera.setOnClickListener(v -> {
            onViewPagerClickListener.onButtonClick(position, true);
        });

        btn_input_type.setText(stepsResponse.getInputDataType());
        btn_input_type.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.poup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                stepsResponse.setInputDataType(menuItem.getTitle().toString());
                stepsResponse.setInputSubmitted(true);
                btn_input_type.setText(menuItem.getTitle().toString());

                Useractivity useractivity = new Useractivity(globlestepsList.get(position).getName(), Constant.userTrackerAction.SELECT.toString(), menuItem.getTitle().toString());
                trackUserActivity(useractivity);

                submitTask(context, stepsResponse, menuItem.getTitle().toString());
                return true;
            });
            popupMenu.show();
        });
        reRenderButtons();

    }

    private void trackUserActivity(Useractivity useractivity) {

        ApiClient.userActivityTracker(useractivity).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
        this.stepsResponse = globlestepsList.get(position);
        reRenderButtons();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }

    private void submitTask(Context context, StepsResponse stepsResponse, String inputType) {

        SurveyRequest surveyRequest = new SurveyRequest();
        surveyRequest.setStepName(stepsResponse.getName());
        surveyRequest.setMenuName(menuName);
        surveyRequest.setProcedureName(procedureName);

        surveyRequest.setStepId(stepsResponse.getId());
        surveyRequest.setInputDataType(inputType);
        ApiClient.createSurvey(context, false, surveyRequest).enqueue(new Callback<SurveyResponse>() {
            @Override
            public void onResponse(Call<SurveyResponse> call, Response<SurveyResponse> response) {
                if (response.isSuccessful()) {
                    getActivity().runOnUiThread(() -> {
                        reRenderButtons();
                    });
                }
            }

            @Override
            public void onFailure(Call<SurveyResponse> call, Throwable t) {
            }

        });

    }


    private void reRenderButtons() {

        if (stepsResponse.isMediaSubmitted()) {
            Drawable img = context.getResources().getDrawable(R.drawable.camera_selected);
            img.setBounds(0, 0, 60, 60);
            btn_camera.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

        } else {
            Drawable img = context.getResources().getDrawable(R.drawable.camera);
            img.setBounds(0, 0, 60, 60);
            btn_camera.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
        }
        if (stepsResponse.isInputSubmitted()) {
            Drawable img = context.getResources().getDrawable(R.drawable.yes_selected);
            img.setBounds(0, 0, 60, 60);
            btn_input_type.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

        } else {
            Drawable img = context.getResources().getDrawable(R.drawable.yes);
            img.setBounds(0, 0, 60, 60);
            btn_input_type.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
        }
        if (stepsResponse.isTextSubmitted()) {
            Drawable img = context.getResources().getDrawable(R.drawable.text_selected);
            img.setBounds(0, 0, 60, 60);
            btnTextAdd.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

        } else {
            Drawable img = context.getResources().getDrawable(R.drawable.text);
            img.setBounds(0, 0, 60, 60);
            btnTextAdd.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
        }

    }
}
