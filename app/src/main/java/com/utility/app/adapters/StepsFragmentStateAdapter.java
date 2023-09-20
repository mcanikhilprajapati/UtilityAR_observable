package com.utility.app.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.utility.app.StepsPageFragment;
import com.utility.app.listener.OnViewPagerClickListener;
import com.utility.app.models.StepsResponse;

import java.util.ArrayList;

public class StepsFragmentStateAdapter extends FragmentStateAdapter implements OnViewPagerClickListener {

    private ArrayList<StepsResponse> arrayList;
    OnViewPagerClickListener onViewPagerClickListener;

    public StepsFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<StepsResponse> courseModalArrayList, OnViewPagerClickListener onViewPagerClickListener) {
        super(fragmentActivity);
        this.arrayList = courseModalArrayList;
        this.onViewPagerClickListener = onViewPagerClickListener;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return new StepsPageFragment(position, position >= arrayList.size() - 1, this);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return this.arrayList.size();
    }

    @Override
    public void onNextClick(int position) {
        onViewPagerClickListener.onNextClick(position);
    }

    @Override
    public void onBackClick(int position) {
        onViewPagerClickListener.onBackClick(position);
    }

    @Override
    public void onHomeClick(int position) {
        onViewPagerClickListener.onHomeClick(position);
    }

    @Override
    public void onButtonClick(int position, boolean image) {
        onViewPagerClickListener.onButtonClick(position,image);
    }
}