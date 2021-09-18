package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.musicplayer.Adapter.ViewPagerAdapter;
import com.example.musicplayer.Fragments.AlbumFragment;
import com.example.musicplayer.Fragments.SongsFragment;
import com.example.musicplayer.Models.MusicFiles;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    ViewPagerAdapter pageAdapter;

    public static final int REQUEST_CODE = 1;
    public static ArrayList<MusicFiles> musicFiles;

    static  boolean shuffleBoolean = false, repeatBoolean = false;
    public static ArrayList<MusicFiles> albums= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permission();




    }

    private void permission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
        }
        else
        {
//            Toast.makeText(this,"Permission Granted !",Toast.LENGTH_SHORT).show();
            musicFiles = getAllAudio(this);
            initViewpager();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
//                Toast.makeText(this,"Permission Granted !",Toast.LENGTH_SHORT).show();
                musicFiles = getAllAudio(this);
                initViewpager();
            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
            }
        }
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

    public static ArrayList<MusicFiles> getAllAudio(Context context)
    {
        ArrayList<String> duplicate = new ArrayList<>();
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String [] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA, //for Path
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID


        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null,null,null);

        if(cursor != null)
        {
            while(cursor.moveToNext())
            {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);

                MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration, id);
                //take log.e for check
                Log.e("Path : "+path, "Album : "+album);
                tempAudioList.add(musicFiles);

                if(!duplicate.contains(album))
                {
                    albums.add(musicFiles);
                    duplicate.add(album);
                }
            }
            cursor.close();
        }
        return tempAudioList;

    }


    //searchView
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem menuItem = menu.findItem(R.id.search_option);
        SearchView searchView = (SearchView) menuItem.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                String userInput = newText.toLowerCase();
                ArrayList<MusicFiles> myFiles = new ArrayList<>();

                for(MusicFiles song : musicFiles)
                {
                    if (song.getTitle().toLowerCase().contains(userInput))
                    {
                        myFiles.add(song);
                    }
                }

                SongsFragment.musicAdapter.updateList(myFiles);
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);

    }


}