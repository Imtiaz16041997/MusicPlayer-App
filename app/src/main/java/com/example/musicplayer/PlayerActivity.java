package com.example.musicplayer;


import static com.example.musicplayer.Adapter.MusicAdapter.mFiles;
import static com.example.musicplayer.AlbumAdapter.AlbumDetailsAdapter.albumFiles;
import static com.example.musicplayer.ApplicationClass.ACTION_PLAY;
import static com.example.musicplayer.ApplicationClass.ACTION_PREVIOUS;
import static com.example.musicplayer.ApplicationClass.CHANNEL_ID_2;
import static com.example.musicplayer.MainActivity.musicFiles;
import static com.example.musicplayer.MainActivity.repeatBoolean;
import static com.example.musicplayer.MainActivity.shuffleBoolean;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicplayer.Interface.ActionPlaying;
import com.example.musicplayer.Models.MusicFiles;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements  ActionPlaying, ServiceConnection {

    //Declaring the Player Activity Views
    TextView song_name,artist_name,duration_played,duration_total;
    ImageView cover_art,nextBtn,prevBtn,backBtn,shuffleBtn,repeatBtn;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;

    int position = -1; //
    public static ArrayList<MusicFiles> listSongs = new ArrayList<>(); // take static , when we get back from playeractivity to -> list of songs
    public static Uri uri;
//    public static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;
    MusicService musicService;
    MediaSessionCompat mediaSessionCompat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFullScreen();
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();


        mediaSessionCompat = new  MediaSessionCompat(getBaseContext(),"My Audio");
        initViews();
        getIntentMethod();

//        song_name.setText(listSongs.get(position).getTitle());
//        artist_name.setText(listSongs.get(position).getArtist());
//        musicService.onCompleted();



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(musicService != null && fromUser)
                {
                    musicService.seekTo(progress * 1000);

                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayerActivity.this.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                if(musicService != null)
                {
                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });

        //Shuffle Button

        shuffleBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(shuffleBoolean)
                {
                    shuffleBoolean = false;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_off);
                }
                else
                {
                    shuffleBoolean = true;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });

        //Repeat Button

        repeatBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(repeatBoolean)
                {
                    repeatBoolean = false;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_off);
                }

                else
                {
                    repeatBoolean = true;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });


    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    private void getIntentMethod() {

        position = getIntent().getIntExtra("position",-1); //  get the position from musicAdapter, pass the default value
        String sender = getIntent().getStringExtra("sender");  //getting extras from sender

        if (sender != null && sender.equals("albumDetails"))
        {
            listSongs = albumFiles;
        }

        else
        {
            listSongs = mFiles;
        }



        if(listSongs != null)
        {
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }

//        if(musicService != null)
//        {
//            musicService.stop();
//            musicService.release();
//            musicService.createMediaPlayer(position);
//            musicService.start();
//        }
//
//            musicService.createMediaPlayer(position);
//            musicService.start();
        showNotification(R.drawable.ic_pause);
        Intent intent = new Intent(this,MusicService.class);
        intent.putExtra("servicePosition",position);
        startService(intent);

//        seekBar.setMax(musicService.getDuration() / 1000);
//        metaData(uri);


    }


    private String formattedTime(int mCurrentPosition)
    {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;

        if(seconds.length() == 1)
        {
            return totalNew;
        }
        else
        {
            return totalOut;
        }
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
    
    private void metaData( Uri uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        retriever.setDataSource(uri.toString());

        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration()) / 1000 ;
        duration_total.setText(formattedTime(durationTotal));

        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap ;
        if(art != null)
        {

            bitmap = BitmapFactory.decodeByteArray(art,0,art.length);

            ImageAnimation(this,cover_art,bitmap);

            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();

                    if(swatch != null)
                    {
                        ImageView gradient = findViewById(R.id.imageViewGradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);

                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);

                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int []{swatch.getRgb(), 0x00000000});
                        gradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int []{swatch.getRgb(), swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);

                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getTitleTextColor());
                    }
                    else
                    {
                        ImageView gradient = findViewById(R.id.imageViewGradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);

                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);

                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int []{0xff000000, 0x00000000});
                        gradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int []{0xff000000, 0xff000000});
                        mContainer.setBackground(gradientDrawableBg);

                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    }
                }
            });

        }
        else
        {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.m2)
                    .into(cover_art);

            ImageView gradient = findViewById(R.id.imageViewGradient);
            RelativeLayout mContainer = findViewById(R.id.mContainer);

            gradient.setBackgroundResource(R.drawable.gradient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);

            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.DKGRAY);

        }
        
    }

    @Override
    protected void onResume() {

        Intent intent = new Intent(this,MusicService.class);
        bindService(intent,this,BIND_AUTO_CREATE);

        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void prevThreadBtn() {
        prevThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    public void prevBtnClicked() {

        if(musicService.isPlaying())
        {
            musicService.stop();
            musicService.release();

            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }

            else if(!shuffleBoolean && !repeatBoolean)
            {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }

            uri = uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());


            seekBar.setMax(musicService.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    if(musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });

            musicService.onCompleted();
            showNotification(R.drawable.ic_pause);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            musicService.start();
        }

        else

        {
            musicService.stop();
            musicService.release();

            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }

            else if(!shuffleBoolean && !repeatBoolean)
            {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }


//            position = ((position + 1) % listSongs.size());
            uri = uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());

            seekBar.setMax(musicService.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    if(musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });

            musicService.onCompleted();
            showNotification(R.drawable.ic_play);
            playPauseBtn.setBackgroundResource(R.drawable.ic_play);

        }
    }

    private void nextThreadBtn() {

        nextThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
        
    }

    public void nextBtnClicked() {
        if(musicService.isPlaying())
        {
            musicService.stop();
            musicService.release();

            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }

            else if(!shuffleBoolean && !repeatBoolean)
            {
                position = ((position + 1) % listSongs.size());

            }
            //else position will be position

//            position = ((position - 1) < 0 ? (listSongs.size()-1) : (position -1));

            uri = uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());

            seekBar.setMax(musicService.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    if(musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });

            musicService.onCompleted();
            showNotification(R.drawable.ic_pause);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            musicService.start();
        }

        else

        {
            musicService.stop();
            musicService.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }

            else if(!shuffleBoolean && !repeatBoolean)
            {
                position = ((position + 1) % listSongs.size());

            }

//            position = ((position - 1) < 0 ? (listSongs.size()-1) : (position -1));
//            position = ((position + 1) % listSongs.size());
            uri = uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());

            seekBar.setMax(musicService.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    if(musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });

            musicService.onCompleted();
            showNotification(R.drawable.ic_play);
            playPauseBtn.setBackgroundResource(R.drawable.ic_play);

        }
    }

    private int getRandom(int i) {
        Random random = new Random();

        return random.nextInt(i + 1);

    }

    private void playThreadBtn() {
        playThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();

    }

    public void playPauseBtnClicked() {
        if(musicService.isPlaying())
        {
            playPauseBtn.setImageResource(R.drawable.ic_play);
            showNotification(R.drawable.ic_play);
            musicService.pause();
            seekBar.setMax(musicService.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    if(musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
        else
        {
            showNotification(R.drawable.ic_pause);
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            musicService.start();
            seekBar.setMax(musicService.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    if(musicService != null)
                    {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }

    //Animation On When music end to next song
    public void ImageAnimation(Context context, final ImageView imageView,final Bitmap bitmap)
    {
        Animation animationOut = AnimationUtils.loadAnimation(context,android.R.anim.fade_out);
        Animation animationIn = AnimationUtils.loadAnimation(context,android.R.anim.fade_in);

        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);

                animationIn.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                imageView.startAnimation(animationIn);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(animationOut);

    }


//    @Override
//    public void onCompletion(MediaPlayer mediaPlayer) {
//
//        nextBtnClicked();
//        if(musicService != null)
//        {
//            musicService.createMediaPlayer(position);
//            musicService.start();
//            musicService.onCompleted();
//
//        }
//
//    }

    //Service

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {

        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        Toast.makeText(this,"Connected" + musicService,Toast.LENGTH_SHORT).show();

                seekBar.setMax(musicService.getDuration() / 1000);
                metaData(uri);

                song_name.setText(listSongs.get(position).getTitle());
                artist_name.setText(listSongs.get(position).getArtist());
                musicService.onCompleted();



    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }

    // notification with broadcasting

    void showNotification(int playPauseBtn)
    {

        Intent intent = new Intent(this,PlayerActivity.class);
        PendingIntent contentIntent = android.app.PendingIntent.getActivity(this,0,intent,0);

        //prev
        Intent prevIntent = new Intent(this,NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = android.app.PendingIntent.getBroadcast(this,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //play
        Intent pauseIntent = new Intent(this,NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending = android.app.PendingIntent.getBroadcast(this,0,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //next
        Intent nextIntent = new Intent(this,NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent nextPending = android.app.PendingIntent.getBroadcast(this,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        //thumbnail

        byte [] picture = null;
        picture = getAlbumArt(musicFiles.get(position).getPath());
        Bitmap thumbnail = null;

        if(picture !=null)
        {
            thumbnail = BitmapFactory.decodeByteArray(picture, 0,picture.length);
        }
        else
        {
            thumbnail = BitmapFactory.decodeResource(getResources(),R.drawable.m2);
        }

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumbnail)
                .setContentTitle(musicFiles.get(position).getPath())
                .setContentText(musicFiles.get(position).getArtist())
                .addAction(R.drawable.ic_skip_previous,"Previous",prevPending)
                .addAction(playPauseBtn,"Pause",pausePending)
                .addAction(R.drawable.ic_skip_next,"Next",nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build();

        NotificationManager notificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0,notification);



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