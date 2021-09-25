package com.example.musicplayer.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.example.musicplayer.MainActivity.ARTIST_TO_FRAGMENT;
import static com.example.musicplayer.MainActivity.PATH_TO_FRAGMENT;
import static com.example.musicplayer.MainActivity.SHOW_MINI_PLAYER;
import static com.example.musicplayer.MainActivity.SONG_NAME_TO_FRAGMENT;
import static com.example.musicplayer.MusicService.MUSIC_FILE;
import static com.example.musicplayer.MusicService.MUSIC_LAST_PLAYED;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.Models.MusicFiles;
import com.example.musicplayer.MusicService;
import com.example.musicplayer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class NowPlayingFragmentBottom extends Fragment implements ServiceConnection {

  ImageView nextBtn, prevBtn, albumArt;
  TextView artist, songName;
  FloatingActionButton playPauseBtn;

  View view;
  MusicService musicService;

    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ARTIST_NAME = "ARTIST NAME";
    public static final String SONG_NAME = "SONG NAME";


    public NowPlayingFragmentBottom() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_now_playing_bottom, container, false);

        artist = view.findViewById(R.id.song_artist_miniPlayer);
        songName = view.findViewById(R.id.song_name_miniPlayer);
        nextBtn = view.findViewById(R.id.skip_next_bottom);
        prevBtn = view.findViewById(R.id.skip_prev_bottom);
        albumArt = view.findViewById(R.id.bottom_album_art);
        playPauseBtn = view.findViewById(R.id.play_pause_miniPlayer);
        //prev , next , playpause

        prevBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(musicService != null){
                    musicService.previousBtnClicked();

                    if(getActivity() != null) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE).edit();
                        editor.putString(MUSIC_FILE, musicService.musicFiles.get(musicService.position).getPath());
                        editor.putString(ARTIST_NAME, musicService.musicFiles.get(musicService.position).getArtist());
                        editor.putString(SONG_NAME, musicService.musicFiles.get(musicService.position).getTitle());
                        editor.apply();

                        SharedPreferences preferences = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED,MODE_PRIVATE);
                        String path = preferences.getString(MUSIC_FILE,null);
                        String artistName = preferences.getString(ARTIST_NAME,null);
                        String song_name = preferences.getString(SONG_NAME,null);

                        if(path != null){
                            SHOW_MINI_PLAYER = true;
                            PATH_TO_FRAGMENT = path;
                            ARTIST_TO_FRAGMENT = artistName;
                            SONG_NAME_TO_FRAGMENT = song_name;

                        }
                        else{
                            SHOW_MINI_PLAYER = false;
                            PATH_TO_FRAGMENT = null;
                            ARTIST_TO_FRAGMENT = null;
                            SONG_NAME_TO_FRAGMENT = null;
                        }

                        if (SHOW_MINI_PLAYER ){

                            if(PATH_TO_FRAGMENT !=null) {

                                byte[] art = getAlbumArt(PATH_TO_FRAGMENT);

                                if(art != null) {
                                    Glide.with(getContext())
                                            .load(art)
                                            .into(albumArt);
                                }
                                else {

                                    Glide.with(getContext())
                                            .load(R.drawable.m2)
                                            .into(albumArt);

                                }
                                songName.setText(SONG_NAME_TO_FRAGMENT);
                                artist.setText(ARTIST_TO_FRAGMENT);

                            }

                        }

                    }

                }

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(musicService != null){
                    musicService.nextBtnClicked();

                    if(getActivity() != null) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE).edit();
                        editor.putString(MUSIC_FILE, musicService.musicFiles.get(musicService.position).getPath());
                        editor.putString(ARTIST_NAME, musicService.musicFiles.get(musicService.position).getArtist());
                        editor.putString(SONG_NAME, musicService.musicFiles.get(musicService.position).getTitle());
                        editor.apply();

                        SharedPreferences preferences = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED,MODE_PRIVATE);
                        String path = preferences.getString(MUSIC_FILE,null);
                        String artistName = preferences.getString(ARTIST_NAME,null);
                        String song_name = preferences.getString(SONG_NAME,null);

                        if(path != null){
                            SHOW_MINI_PLAYER = true;
                            PATH_TO_FRAGMENT = path;
                            ARTIST_TO_FRAGMENT = artistName;
                            SONG_NAME_TO_FRAGMENT = song_name;

                        }
                        else{
                            SHOW_MINI_PLAYER = false;
                            PATH_TO_FRAGMENT = null;
                            ARTIST_TO_FRAGMENT = null;
                            SONG_NAME_TO_FRAGMENT = null;
                        }

                        if (SHOW_MINI_PLAYER){

                            if(PATH_TO_FRAGMENT !=null) {

                                byte[] art = getAlbumArt(PATH_TO_FRAGMENT);

                                if(art != null) {
                                    Glide.with(getContext())
                                            .load(art)
                                            .into(albumArt);
                                }
                                else {

                                    Glide.with(getContext())
                                            .load(R.drawable.m2)
                                            .into(albumArt);

                                }
                                songName.setText(SONG_NAME_TO_FRAGMENT);
                                artist.setText(ARTIST_TO_FRAGMENT);

                            }

                        }

                    }

                }
            }
        });

        playPauseBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(musicService != null){

                    musicService.playPauseBtnClicked();

                    if(musicService.isPlaying()){
                        playPauseBtn.setImageResource(R.drawable.ic_pause);
                    }
                    else{
                        playPauseBtn.setImageResource(R.drawable.ic_play);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SHOW_MINI_PLAYER){

            if(PATH_TO_FRAGMENT !=null) {

                byte[] art = getAlbumArt(PATH_TO_FRAGMENT);

                if(art != null) {
                    Glide.with(getContext())
                            .load(art)
                            .into(albumArt);
                }
                else {

                    Glide.with(getContext())
                            .load(R.drawable.m2)
                            .into(albumArt);

                }
                songName.setText(SONG_NAME_TO_FRAGMENT);
                artist.setText(ARTIST_TO_FRAGMENT);
                Intent intent = new Intent(getContext(),MusicService.class);
                if(getContext() != null){
                    getContext().bindService(intent,this, Context.BIND_AUTO_CREATE);
                }
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getContext() != null ){
            getContext().unbindService(this);
        }

    }

    private  byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return  art;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }
}