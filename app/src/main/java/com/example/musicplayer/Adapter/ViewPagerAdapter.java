package com.example.musicplayer.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fragments;
    private  ArrayList<String> titles;

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<>();
        this.titles = new ArrayList<>();
    }

//    Creating the add fragments method that will add each fragment and title to the viewpager
    public void addFragments(Fragment fragment, String title){
        fragments.add(fragment);
        titles.add(title);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
//        return super.getPageTitle(position);
        return titles.get(position);
    }


    //    ArrayList<Fragment> fragments;
//    private  ArrayList<String> titles;
//
//    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
//        super(fragmentManager, lifecycle);
//
//        this.fragments = new ArrayList<>();
//        this.titles = new ArrayList<>();
//    }
//
//    //Creating the add fragments method that will add each fragment and title to the viewpager
//    public void addFragments(Fragment fragment, String title){
//        fragments.add(fragment);
//        titles.add(title);
//    }
//
//
//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//        return fragments.get(position);
//    }
//
//    @Override
//    public int getItemCount() {
//        return fragments.size();
//    }
}
