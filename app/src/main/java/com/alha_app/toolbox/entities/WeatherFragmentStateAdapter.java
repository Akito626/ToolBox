package com.alha_app.toolbox.entities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.alha_app.toolbox.DailyWeatherFragment;
import com.alha_app.toolbox.TodayWeatherFragment;

public class WeatherFragmentStateAdapter extends FragmentStateAdapter {
    public WeatherFragmentStateAdapter(FragmentActivity fragment){
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position){
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new TodayWeatherFragment();
                break;
            case 1:
                fragment = new DailyWeatherFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount(){
        return 2;
    }
}
