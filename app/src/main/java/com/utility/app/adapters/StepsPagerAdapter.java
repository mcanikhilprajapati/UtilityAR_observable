package com.utility.app.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.utility.app.listener.OnViewPagerClickListener;
import com.utility.app.models.StepsResponse;
import com.utilityar.app.R;

import java.util.ArrayList;


public class StepsPagerAdapter extends RecyclerView.Adapter<StepsPagerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<StepsResponse> arrayList;
    private OnViewPagerClickListener onViewPagerClickListener;

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
        if (stepsResponse.getMediaType().equals("IMAGE")) {
            holder.img_task_image.setVisibility(View.VISIBLE);
            Glide.with(context).load(stepsResponse.getMedia()).into(holder.img_task_image);
        }
        if (stepsResponse.getMediaType().equals("VIDEO")) {
            holder.videoView.setVisibility(View.VISIBLE);
            holder.videoView.setVideoPath(stepsResponse.getMedia()); //the string of the URL mentioned above
            holder.videoView.requestFocus();
            holder.videoView.start();
            holder.videoView.canSeekBackward();
            holder.videoView.canPause();
            holder.videoView.canSeekForward();
        }
        if (position == 0)
            holder.btn_back.setVisibility(View.INVISIBLE);
        if (position == arrayList.size() - 1)
            holder.btn_next.setVisibility(View.INVISIBLE);

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

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.
        private Button btn_next, btn_back, btn_home;
        private TextView txt_step_name, txt_description;
        private AppCompatImageView img_task_image;
        private VideoView videoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            txt_step_name = itemView.findViewById(R.id.txt_step_name);
            txt_description = itemView.findViewById(R.id.txt_description);
            btn_next = itemView.findViewById(R.id.btn_next);
            btn_back = itemView.findViewById(R.id.btn_back);
            btn_home = itemView.findViewById(R.id.btn_home);
            img_task_image = itemView.findViewById(R.id.img_task_image);
            videoView = itemView.findViewById(R.id.videoView);

        }
    }
}
