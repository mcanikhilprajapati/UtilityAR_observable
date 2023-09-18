package com.utility.app.listener;

public interface OnViewPagerClickListener {
    void onNextClick(int position);
    void onBackClick(int position);
    void onHomeClick(int position);
    void onButtonClick(int position,boolean image);

}
