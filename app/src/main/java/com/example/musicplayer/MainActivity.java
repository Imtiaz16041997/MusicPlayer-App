package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.musicplayer.Adapter.ViewPagerAdapter;
import com.example.musicplayer.Fragments.AlbumFragment;
import com.example.musicplayer.Fragments.SongsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    ViewPagerAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewpager();
    }

    private void initViewpager() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        fragmentManager = getSupportFragmentManager();
        pageAdapter = new ViewPagerAdapter(fragmentManager);

        pageAdapter.addFragments(new SongsFragment(), "Songs");
        pageAdapter.addFragments(new AlbumFragment(),"Albums");
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);





    }





}