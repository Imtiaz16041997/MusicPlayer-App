package com.example.musicplayer.Fragments;

import static com.example.musicplayer.MainActivity.ARTIST_TO_FRAGMENT;
import static com.example.musicplayer.MainActivity.PATH_TO_FRAGMENT;
import static com.example.musicplayer.MainActivity.SHOW_MINI_PLAYER;
import static com.example.musicplayer.MainActivity.SONG_NAME_TO_FRAGMENT;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class NowPlayingFragmentBottom extends Fragment {

  ImageView nextBtn, prevBtn, albumArt;
  TextView artist, songName;
  FloatingActionButton playPauseBtn;

  View view;



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

            }

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
}