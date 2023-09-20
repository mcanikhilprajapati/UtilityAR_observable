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
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.media3.common.MediaItem;
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


public class StepsPagerAdapter extends RecyclerView.Adapter<StepsPagerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<StepsResponse> arrayList;
    private OnViewPagerClickListener onViewPagerClickListener;
    ExoPlayer player;

    public StepsPagerAdapter(Context context, ArrayList<StepsResponse> courseModalArrayList, OnViewPagerClickListener onViewPagerClickListener) {
        this.context = context;
        this.arrayList = courseModalArrayList;
        this.onViewPagerClickListener = onViewPagerClickListener;
        player = new ExoPlayer.Builder(context).build();

    }

    @NonNull
    @Override
    public StepsPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_item_viewpager_steps, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull StepsPagerAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        StepsResponse stepsResponse = arrayList.get(position);
        holder.txt_step_name.setText(stepsResponse.getName());
        if (!TextUtils.isEmpty(stepsResponse.getDescription())) {
            holder.txt_description.setText(stepsResponse.getDescription());
            holder.txt_description.setVisibility(View.VISIBLE);
            holder.sc_txt_description.setVisibility(View.VISIBLE);
        } else {
            holder.txt_description.setVisibility(View.GONE);
            holder.sc_txt_description.setVisibility(View.GONE);
        }

        if (position == arrayList.size() - 1) {
            holder.btn_next.setText("Finish");
        } else {
            holder.btn_next.setText("Next");
        }
        holder.img_task_image.setVisibility(View.GONE);
        holder.webview.setVisibility(View.GONE);
        holder.playerView.setVisibility(View.GONE);


        if (stepsResponse.getMediaType().equals("URL")) {
//            holder.txt_description.setVisibility(View.GONE);
//            holder.bottom_layout.setVisibility(View.GONE);
            holder.webview.setVisibility(View.VISIBLE);

            WebSettings webSettings=holder.webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
//            holder.webview.clearCache(true);
//            holder.webview.clearHistory();
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            holder.webview.loadUrl(stepsResponse.getMedia());
            holder.webview.setWebChromeClient(new WebChromeClient());

        }
        if (stepsResponse.getMediaType().equals("IMAGE")) {
            holder.img_task_image.setVisibility(View.VISIBLE);
//            Glide.with(context).load(stepsResponse.getMedia()).into(holder.img_task_image);

            Glide.with(context)
                    .asBitmap()      //get hieght and width
                    .load(stepsResponse.getMedia())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap thumbnail, @Nullable
                        Transition<? super Bitmap> transition) {

                            try {
                                int height = holder.img_task_image.getLayoutParams().height;
                                int newWidth = (height * thumbnail.getWidth()) / thumbnail.getHeight();

                                holder.img_task_image.getLayoutParams().width = newWidth;
                                holder.img_task_image.getLayoutParams().height = height;
                                holder.img_task_image.setImageBitmap(thumbnail);
                            } catch (Exception exception) {
                            }
                        }
                    });
        }
        if (stepsResponse.getMediaType().equals("VIDEO")) {
            holder.playerView.setVisibility(View.VISIBLE);
            holder.playerView.setFocusable(true);

            holder.playerView.setPlayer(player);
            holder.playerView.setUseController(true);
            MediaItem mediaItem = MediaItem.fromUri(stepsResponse.getMedia());
            player.setMediaItem(mediaItem);
            player.prepare();
//            player.play();
            
            holder.playerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    if (holder.playerView != null)
                        holder.playerView.getPlayer().setPlayWhenReady(true);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    if (holder.playerView != null)
                        holder.playerView.getPlayer().setPlayWhenReady(false);

                }
            });
        } else {

        }


        holder.btnTextAdd.setOnClickListener(v -> {
            if (onViewPagerClickListener != null) {
                onViewPagerClickListener.onButtonClick(position, false);
            }
        });

        holder.btn_back.setOnClickListener(v -> {
            if (onViewPagerClickListener != null) {
                onViewPagerClickListener.onBackClick(position);
            }
        });
        holder.btn_next.setOnClickListener(v -> {
            if (onViewPagerClickListener != null) {
                onViewPagerClickListener.onNextClick(position);
            }
        });
        holder.btn_home.setOnClickListener(v -> {
            if (onViewPagerClickListener != null) {
                onViewPagerClickListener.onHomeClick(position);
            }
        });


        holder.btn_camera.setOnClickListener(v -> {
            onViewPagerClickListener.onButtonClick(position, true);
        });

        holder.btn_input_type.setText(stepsResponse.getInputDataType());
        holder.btn_input_type.setOnClickListener(v -> {
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
            holder.btn_camera.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

        } else {
            Drawable img = context.getResources().getDrawable(R.drawable.camera);
            img.setBounds(0, 0, 60, 60);
            holder.btn_camera.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
        }
        if (stepsResponse.isInputSubmitted()) {
            Drawable img = context.getResources().getDrawable(R.drawable.yes_selected);
            img.setBounds(0, 0, 60, 60);
            holder.btn_input_type.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

        } else {
            Drawable img = context.getResources().getDrawable(R.drawable.yes);
            img.setBounds(0, 0, 60, 60);
            holder.btn_input_type.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
        }
        if (stepsResponse.isTextSubmitted()) {
            Drawable img = context.getResources().getDrawable(R.drawable.text_selected);
            img.setBounds(0, 0, 60, 60);
            holder.btnTextAdd.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

        } else {
            Drawable img = context.getResources().getDrawable(R.drawable.text);
            img.setBounds(0, 0, 60, 60);
            holder.btnTextAdd.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
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
        private PlayerView playerView;
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
            playerView = itemView.findViewById(R.id.videoView);
            btnTextAdd = itemView.findViewById(R.id.btnTextAdd);
            btn_camera = itemView.findViewById(R.id.btn_camera);
            btn_input_type = itemView.findViewById(R.id.btn_input_type);
            webview = itemView.findViewById(R.id.webview);
            sc_txt_description = itemView.findViewById(R.id.sc_txt_description);

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

    private void pausePlayer() {
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }

    private void startPlayer() {
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

}
