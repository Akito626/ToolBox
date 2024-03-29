package com.alha_app.toolbox.entities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.alha_app.toolbox.WeatherMapFragment;
import com.alha_app.toolbox.WeatherFragment;

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
                fragment = new WeatherFragment();
                break;
            case 1:
                fragment = new WeatherMapFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount(){
        return 2;
    }
}
