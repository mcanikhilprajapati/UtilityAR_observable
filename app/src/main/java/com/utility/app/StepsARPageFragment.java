package com.utility.app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.utility.app.listener.OnViewPagerClickListener;
import com.utility.app.models.StepsResponse;
import com.utilityar.app.R;

public class StepsARPageFragment extends Fragment {
    private OnViewPagerClickListener onViewPagerClickListener;
    StepsResponse stepsResponse;
        private Button btn_next, btn_back, btn_home;
    private Context context;
    private boolean isLast = false;
    private int position = 0;

    public StepsARPageFragment() {

    }

    public StepsARPageFragment(StepsResponse stepsResponse, int position, boolean isLast, OnViewPagerClickListener onViewPagerClickListener) {
        this.stepsResponse = stepsResponse;
        this.isLast = isLast;
        this.position = position;
        this.onViewPagerClickListener = onViewPagerClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(
                R.layout.row_item_ar_viewpager_steps, container, false);
        context = itemView.getContext();
        btn_next = itemView.findViewById(R.id.btn_next);
        btn_back = itemView.findViewById(R.id.btn_back);
        btn_home = itemView.findViewById(R.id.btn_home);
        return itemView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
    }

}
