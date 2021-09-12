package com.example.musicplayer;

import static com.example.musicplayer.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.musicplayer.Models.MusicFiles;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    //Declaring the Player Activity Views
    TextView song_name,artist_name,duration_played,duration_total;
    ImageView cover_art,nextBtn,prevBtn,backBtn,shuffleBtn,repeatBtn;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;

    int position = -1; //
    static ArrayList<MusicFiles> listSongs = new ArrayList<>(); // take static , when we get back from playeractivity to -> list of songs
    static Uri uri;
    static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initViews();
        getIntentMethod();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(mediaPlayer != null && fromUser)
                {
                    mediaPlayer.seekTo(progress * 1000);
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void getIntentMethod() {

        position = getIntent().getIntExtra("position",-1); //  get the position from musicAdapter, pass the default value
        listSongs = musicFiles;

        if(listSongs != null)
        {
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration() / 1000);


    }

    private void initViews() {
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.durationPlayed);
        duration_total = findViewById(R.id.durationTotal);
        cover_art = findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.id_next);
        prevBtn = findViewById(R.id.id_prev);
        backBtn = findViewById(R.id.back_btn);
        shuffleBtn = findViewById(R.id.id_shuffle);
        repeatBtn = findViewById(R.id.id_repeat);
        playPauseBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);

    }
}