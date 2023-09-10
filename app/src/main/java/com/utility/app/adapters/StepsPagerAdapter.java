package com.utility.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
    String[] inputType = {"Priority", "Yes", "No", "Na"};

    // creating a constructor class.
    public StepsPagerAdapter(Context context, ArrayList<StepsResponse> courseModalArrayList, OnViewPagerClickListener onViewPagerClickListener) {
        this.context = context;
        this.arrayList = courseModalArrayList;
        this.onViewPagerClickListener = onViewPagerClickListener;
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
        holder.txt_description.setText(stepsResponse.getDescription());

        holder.img_task_image.setVisibility(View.GONE);
        holder.txt_url.setVisibility(View.GONE);
        holder.playerView.setVisibility(View.GONE);

        if (stepsResponse.getMediaType().equals("URL")) {
            holder.txt_url.setVisibility(View.VISIBLE);

            holder.txt_url.setText(stepsResponse.getMedia());
            holder.txt_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(stepsResponse.getMedia()));
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(i);
                    } catch (Exception e) {
                    }
                }
            });
        }
        if (stepsResponse.getMediaType().equals("IMAGE")) {
            holder.img_task_image.setVisibility(View.VISIBLE);
            Glide.with(context).load(stepsResponse.getMedia()).into(holder.img_task_image);
        }
        if (stepsResponse.getMediaType().equals("VIDEO")) {
            holder.playerView.setVisibility(View.VISIBLE);
            holder.playerView.setFocusable(true);
            ExoPlayer player = new ExoPlayer.Builder(context).build();
            holder.playerView.setPlayer(player);
//            holder.playerView.setUseController(true);
            MediaItem mediaItem = MediaItem.fromUri(stepsResponse.getMedia());
            player.setMediaItem(mediaItem);
            player.prepare();
//            player.play();//Set auto play true if require
//            holder.playerView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (holder.bottom_layout.getVisibility() == View.GONE) {
//                        holder.bottom_layout.setVisibility(View.VISIBLE);
//                        holder.txt_description.setVisibility(View.VISIBLE);
//                    }
//                    else {
//                        holder.txt_description.setVisibility(View.GONE);
//                        holder.bottom_layout.setVisibility(View.GONE);
//                    }
//                }
//            });

        }
        holder.btnTextAdd.setOnClickListener(v -> {
            if (onViewPagerClickListener != null) {
                onViewPagerClickListener.onTextButtonClick(position);
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
        setupMainmenuSpinner(holder, arrayList.get(position));
    }

    private void setupMainmenuSpinner(ViewHolder holder, StepsResponse stepsResponse) {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, inputType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        holder.spinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                    submitTask(context, stepsResponse, inputType[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        holder.spinnerMenu.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.
        private Button btn_next, btn_back, btn_home, btnTextAdd;
        private TextView txt_step_name, txt_description, txt_url;
        private AppCompatImageView img_task_image;
        private LinearLayoutCompat bottom_layout;
        private PlayerView playerView;
        private Spinner spinnerMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            txt_url = itemView.findViewById(R.id.txt_url);
            txt_step_name = itemView.findViewById(R.id.txt_step_name);
            txt_description = itemView.findViewById(R.id.txt_description);
            btn_next = itemView.findViewById(R.id.btn_next);
            btn_back = itemView.findViewById(R.id.btn_back);
            btn_home = itemView.findViewById(R.id.btn_home);
            img_task_image = itemView.findViewById(R.id.img_task_image);
            playerView = itemView.findViewById(R.id.videoView);
            btnTextAdd = itemView.findViewById(R.id.btnTextAdd);
            bottom_layout = itemView.findViewById(R.id.bottom_layout);
            spinnerMenu = itemView.findViewById(R.id.sp_mainmenu);

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
