package com.utility.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.utility.app.listener.OnViewPagerClickListener;
import com.utility.app.models.StepsResponse;
import com.utility.app.models.SurveyResponse;
import com.utility.app.models.request.SurveyRequest;
import com.utility.app.retrofit.ApiClient;
import com.utilityar.app.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StepsPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<StepsResponse> arrayList;
    private OnViewPagerClickListener onViewPagerClickListener;


    public StepsPagerAdapter(Context context, ArrayList<StepsResponse> courseModalArrayList, OnViewPagerClickListener onViewPagerClickListener) {
        this.context = context;
        this.arrayList = courseModalArrayList;
        this.onViewPagerClickListener = onViewPagerClickListener;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new VideoViewHolder(LayoutInflater.from(context).inflate(R.layout.row_item_video_viewpager_steps, parent, false));
        else
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_item_viewpager_steps, parent, false));
    }


    @Override
    public int getItemViewType(int position) {
        if (arrayList.get(position).getMediaType().equals("VIDEO"))
            return 0;
        else
            return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            StepsResponse stepsResponse = arrayList.get(position);

            viewHolder.txt_step_name.setText(stepsResponse.getName());
            if (!TextUtils.isEmpty(stepsResponse.getDescription())) {
                viewHolder.txt_description.setText(stepsResponse.getDescription());
                viewHolder.txt_description.setVisibility(View.VISIBLE);
                viewHolder.sc_txt_description.setVisibility(View.VISIBLE);
            } else {
                viewHolder.txt_description.setVisibility(View.GONE);
                viewHolder.sc_txt_description.setVisibility(View.GONE);
            }

            if (position == arrayList.size() - 1) {
                viewHolder.btn_next.setText("Finish");
            } else {
                viewHolder.btn_next.setText("Next");
            }
            viewHolder.img_task_image.setVisibility(View.GONE);
            viewHolder.webview.setVisibility(View.GONE);


            if (stepsResponse.getMediaType().equals("URL")) {
                viewHolder.webview.setVisibility(View.VISIBLE);
                WebSettings webSettings = viewHolder.webview.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                viewHolder.webview.loadUrl(stepsResponse.getMedia());
                viewHolder.webview.setWebChromeClient(new WebChromeClient());

            }
            if (stepsResponse.getMediaType().equals("IMAGE")) {
                viewHolder.img_task_image.setVisibility(View.VISIBLE);

                Glide.with(context)
                        .asBitmap()      //get hieght and width
                        .load(stepsResponse.getMedia())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap thumbnail, @Nullable
                            Transition<? super Bitmap> transition) {

                                try {
                                    int height = viewHolder.img_task_image.getLayoutParams().height;
                                    int newWidth = (height * thumbnail.getWidth()) / thumbnail.getHeight();

                                    viewHolder.img_task_image.getLayoutParams().width = newWidth;
                                    viewHolder.img_task_image.getLayoutParams().height = height;
                                    viewHolder.img_task_image.setImageBitmap(thumbnail);
                                } catch (Exception exception) {
                                }
                            }
                        });
            }

            viewHolder.btnTextAdd.setOnClickListener(v -> {
                if (onViewPagerClickListener != null) {
                    onViewPagerClickListener.onButtonClick(position, false);
                }
            });

            viewHolder.btn_back.setOnClickListener(v -> {
                if (onViewPagerClickListener != null) {
                    onViewPagerClickListener.onBackClick(position);
                }
            });
            viewHolder.btn_next.setOnClickListener(v -> {
                if (onViewPagerClickListener != null) {
                    onViewPagerClickListener.onNextClick(position);
                }
            });
            viewHolder.btn_home.setOnClickListener(v -> {
                if (onViewPagerClickListener != null) {
                    onViewPagerClickListener.onHomeClick(position);
                }
            });


            viewHolder.btn_camera.setOnClickListener(v -> {
                onViewPagerClickListener.onButtonClick(position, true);
            });

            viewHolder.btn_input_type.setText(stepsResponse.getInputDataType());
            viewHolder.btn_input_type.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.poup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    arrayList.get(position).setInputDataType(menuItem.getTitle().toString());
                    arrayList.get(position).setInputSubmitted(true);
                    notifyItemChanged(position);
                    submitTask(context, stepsResponse, menuItem.getTitle().toString());
                    return true;
                });
                popupMenu.show();
            });


            if (stepsResponse.isMediaSubmitted()) {
                Drawable img = context.getResources().getDrawable(R.drawable.camera_selected);
                img.setBounds(0, 0, 60, 60);
                viewHolder.btn_camera.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

            } else {
                Drawable img = context.getResources().getDrawable(R.drawable.camera);
                img.setBounds(0, 0, 60, 60);
                viewHolder.btn_camera.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
            }
            if (stepsResponse.isInputSubmitted()) {
                Drawable img = context.getResources().getDrawable(R.drawable.yes_selected);
                img.setBounds(0, 0, 60, 60);
                viewHolder.btn_input_type.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

            } else {
                Drawable img = context.getResources().getDrawable(R.drawable.yes);
                img.setBounds(0, 0, 60, 60);
                viewHolder.btn_input_type.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
            }
            if (stepsResponse.isTextSubmitted()) {
                Drawable img = context.getResources().getDrawable(R.drawable.text_selected);
                img.setBounds(0, 0, 60, 60);
                viewHolder.btnTextAdd.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

            } else {
                Drawable img = context.getResources().getDrawable(R.drawable.text);
                img.setBounds(0, 0, 60, 60);
                viewHolder.btnTextAdd.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
            }
        } else if (holder instanceof VideoViewHolder) {
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            StepsResponse stepsResponse = arrayList.get(position);

            videoViewHolder.txt_step_name.setText(stepsResponse.getName());
            if (!TextUtils.isEmpty(stepsResponse.getDescription())) {
                videoViewHolder.txt_description.setText(stepsResponse.getDescription());
                videoViewHolder.txt_description.setVisibility(View.VISIBLE);
                videoViewHolder.sc_txt_description.setVisibility(View.VISIBLE);
            } else {
                videoViewHolder.txt_description.setVisibility(View.GONE);
                videoViewHolder.sc_txt_description.setVisibility(View.GONE);
            }

            if (position == arrayList.size() - 1) {
                videoViewHolder.btn_next.setText("Finish");
            } else {
                videoViewHolder.btn_next.setText("Next");
            }


            videoViewHolder.btnTextAdd.setOnClickListener(v -> {
                if (onViewPagerClickListener != null) {
                    onViewPagerClickListener.onButtonClick(position, false);
                }
            });

            videoViewHolder.btn_back.setOnClickListener(v -> {
                if (onViewPagerClickListener != null) {
                    onViewPagerClickListener.onBackClick(position);
                }
            });
            videoViewHolder.btn_next.setOnClickListener(v -> {
                if (onViewPagerClickListener != null) {
                    onViewPagerClickListener.onNextClick(position);
                }
            });
            videoViewHolder.btn_home.setOnClickListener(v -> {
                if (onViewPagerClickListener != null) {
                    onViewPagerClickListener.onHomeClick(position);
                }
            });


            videoViewHolder.btn_camera.setOnClickListener(v -> {
                onViewPagerClickListener.onButtonClick(position, true);
            });

            videoViewHolder.btn_input_type.setText(stepsResponse.getInputDataType());
            videoViewHolder.btn_input_type.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.poup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    arrayList.get(position).setInputDataType(menuItem.getTitle().toString());
                    arrayList.get(position).setInputSubmitted(true);
                    notifyItemChanged(position);
                    submitTask(context, stepsResponse, menuItem.getTitle().toString());
                    return true;
                });
                popupMenu.show();
            });


            if (stepsResponse.isMediaSubmitted()) {
                Drawable img = context.getResources().getDrawable(R.drawable.camera_selected);
                img.setBounds(0, 0, 60, 60);
                videoViewHolder.btn_camera.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

            } else {
                Drawable img = context.getResources().getDrawable(R.drawable.camera);
                img.setBounds(0, 0, 60, 60);
                videoViewHolder.btn_camera.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
            }
            if (stepsResponse.isInputSubmitted()) {
                Drawable img = context.getResources().getDrawable(R.drawable.yes_selected);
                img.setBounds(0, 0, 60, 60);
                videoViewHolder.btn_input_type.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

            } else {
                Drawable img = context.getResources().getDrawable(R.drawable.yes);
                img.setBounds(0, 0, 60, 60);
                videoViewHolder.btn_input_type.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
            }
            if (stepsResponse.isTextSubmitted()) {
                Drawable img = context.getResources().getDrawable(R.drawable.text_selected);
                img.setBounds(0, 0, 60, 60);
                videoViewHolder.btnTextAdd.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

            } else {
                Drawable img = context.getResources().getDrawable(R.drawable.text);
                img.setBounds(0, 0, 60, 60);
                videoViewHolder.btnTextAdd.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
            }


            videoViewHolder.playerView.setUseController(false);
            MediaItem mediaItem = MediaItem.fromUri(stepsResponse.getMedia());
            videoViewHolder.player.setMediaItem(mediaItem);
            videoViewHolder.player.prepare();


            videoViewHolder.playerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    Toast.makeText(context, "start", Toast.LENGTH_SHORT).show();
                    if (videoViewHolder.playerView != null) {
                        videoViewHolder.player.setPlayWhenReady(true);
                        videoViewHolder.player.getPlaybackState();
                    }
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    Toast.makeText(context, "stop", Toast.LENGTH_SHORT).show();
                    if (videoViewHolder.playerView != null) {
                        videoViewHolder.player.setPlayWhenReady(false);
                        videoViewHolder.player.getPlaybackState();
                    }


                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button btn_next, btn_back, btn_home, btnTextAdd, btn_camera, btn_input_type;
        private TextView txt_step_name, txt_description;
        private AppCompatImageView img_task_image;
        private WebView webview;
        private ScrollView sc_txt_description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
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

        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements Player.Listener {
        private Button btn_next, btn_back, btn_home, btnTextAdd, btn_camera, btn_input_type;
        private TextView txt_step_name, txt_description;
        private PlayerView playerView;
        private ScrollView sc_txt_description;
        ExoPlayer player;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            txt_step_name = itemView.findViewById(R.id.txt_toolbar_title);
            txt_description = itemView.findViewById(R.id.txt_description);
            btn_next = itemView.findViewById(R.id.btn_next);
            btn_back = itemView.findViewById(R.id.btn_back);
            btn_home = itemView.findViewById(R.id.btn_home);
            playerView = itemView.findViewById(R.id.videoView);
            btnTextAdd = itemView.findViewById(R.id.btnTextAdd);
            btn_camera = itemView.findViewById(R.id.btn_camera);
            btn_input_type = itemView.findViewById(R.id.btn_input_type);
            sc_txt_description = itemView.findViewById(R.id.sc_txt_description);
            player = new ExoPlayer.Builder(itemView.getContext()).build();
            playerView.setPlayer(player);
            player.addListener(this);
        }

        @Override
        public void onEvents(Player player, Player.Events events) {
            Player.Listener.super.onEvents(player, events);
        }

    }

    private void submitTask(Context context, StepsResponse stepsResponse, String inputType) {

        SurveyRequest surveyRequest = new SurveyRequest();
        surveyRequest.setStepId(stepsResponse.getId());
        surveyRequest.setInputDataType(inputType);
        ApiClient.createSurvey(context, false, surveyRequest).enqueue(new Callback<SurveyResponse>() {
            @Override
            public void onResponse(Call<SurveyResponse> call, Response<SurveyResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Submitted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SurveyResponse> call, Throwable t) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }

        });

    }


}
