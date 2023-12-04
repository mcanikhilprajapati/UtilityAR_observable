package com.utility.app.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.utility.app.ArFragment;
import com.utility.app.StepsPageFragment;
import com.utility.app.listener.OnViewPagerClickListener;
import com.utility.app.models.StepsResponse;

import java.util.ArrayList;

public class StepsFragmentStateAdapter extends FragmentStateAdapter {

    private ArrayList<StepsResponse> arrayList;
    OnViewPagerClickListener onViewPagerClickListener;
    private String menuName = "";
    private String procedureName = "";

    public StepsFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<StepsResponse> courseModalArrayList, String menuName, String procedureName, OnViewPagerClickListener onViewPagerClickListener) {
        super(fragmentActivity);
        this.arrayList = courseModalArrayList;
        this.onViewPagerClickListener = onViewPagerClickListener;
        this.menuName = menuName;
        this.procedureName = procedureName;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (arrayList.get(position).getMediaType().equalsIgnoreCase("AR")) {
            return new ArFragment(arrayList.get(position), position, position >= arrayList.size() - 1, onViewPagerClickListener);
        } else {
            return new StepsPageFragment(arrayList.get(position), position, position >= arrayList.size() - 1, menuName, procedureName, onViewPagerClickListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return this.arrayList.size();
    }

}