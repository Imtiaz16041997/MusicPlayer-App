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
import android.content.SharedPreferences;
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
import com.example.musicplayer.Interface.ActionPlaying;
import com.example.musicplayer.Models.MusicFiles;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {
    FragmentManager fragmentManager;
    ViewPagerAdapter pageAdapter;

    public static final int REQUEST_CODE = 1;
    public static ArrayList<MusicFiles> musicFiles;

    static  boolean shuffleBoolean = false, repeatBoolean = false;
    public static ArrayList<MusicFiles> albums= new ArrayList<>();

    private  String MY_SORT_PREF = "SortOrder" ;   // String

    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";    //SharedPreference for Last played song save
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static boolean SHOW_MINI_PLAYER = false;
    public static  String PATH_TO_FRAGMENT = null;
    public static final String ARTIST_NAME = "ARTIST NAME";
    public static final String SONG_NAME = "SONG NAME";

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

    public  ArrayList<MusicFiles> getAllAudio(Context context)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);
        String sortOrder = sharedPreferences.getString("sorting","sortByDate");  // make a default value in s1
        ArrayList<String> duplicate = new ArrayList<>();
        albums.clear();
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        String order = null; //Sort Order
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        switch (sortOrder)   // for checking which of the item is selected at this moment
        {
            case "sortByName":
                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;

            case "sortByDate":
                order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                break;

            case "sortBySize":
                order = MediaStore.MediaColumns.SIZE + " DESC";
                break;

        }
        String [] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA, //for Path
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID


        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null,null, order);

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

    //Sorting
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE).edit();

        switch (item.getItemId())
        {
            case R.id.by_name:

                editor.putString("sorting","sortByName");
                editor.apply();
                this.recreate();
                break;

            case R.id.by_date:
                editor.putString("sorting","sortByDate");
                editor.apply();
                this.recreate();
                break;

            case R.id.by_size:
                editor.putString("sorting","sortBySize");
                editor.apply();
                this.recreate();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(MUSIC_LAST_PLAYED,MODE_PRIVATE);
        String value = preferences.getString(MUSIC_FILE,null);

        if(value != null){
            SHOW_MINI_PLAYER = true;
            PATH_TO_FRAGMENT = value;
        }
        else{
            SHOW_MINI_PLAYER = false;
            PATH_TO_FRAGMENT = null;
        }


    }
}