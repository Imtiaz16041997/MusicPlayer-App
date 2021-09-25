package com.example.musicplayer;

import static com.example.musicplayer.ApplicationClass.ACTION_NEXT;
import static com.example.musicplayer.ApplicationClass.ACTION_PLAY;
import static com.example.musicplayer.ApplicationClass.ACTION_PREVIOUS;
import static com.example.musicplayer.ApplicationClass.CHANNEL_ID_2;
import static com.example.musicplayer.PlayerActivity.listSongs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.musicplayer.Interface.ActionPlaying;
import com.example.musicplayer.Models.MusicFiles;

import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    public ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    public int position = -1;

    ActionPlaying actionPlaying;
    MediaSessionCompat mediaSessionCompat;
    //SharedPerference
    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ARTIST_NAME = "ARTIST NAME";
    public static final String SONG_NAME = "SONG NAME";

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(),"My Audio");
//        musicFiles = listSongs;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition",-1);
        String actionName = intent.getStringExtra("ActionName");
        if(myPosition != -1)
        {
            playMedia(myPosition);

        }

        if(actionName != null)
        {
            switch(actionName)
            {
                case "playPause":
                    Toast.makeText(this,"PlayPause",Toast.LENGTH_SHORT).show();
                    playPauseBtnClicked();
                    break;

                case "next":
                    Toast.makeText(this,"Next",Toast.LENGTH_SHORT).show();
                    nextBtnClicked();
                    break;

                case "previous":
                    Toast.makeText(this,"Previous",Toast.LENGTH_SHORT).show();
                    previousBtnClicked();
                    break;
            }
        }
        return START_STICKY;
    }

    private void playMedia(int StartPosition) {
        musicFiles = listSongs;
        position = StartPosition;
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();

            if(musicFiles != null)
            {
                createMediaPlayer(position);
                mediaPlayer.start();
            }

        }
        else
        {
            createMediaPlayer(position);
            mediaPlayer.start();

        }
    }

    public class MyBinder extends Binder {

        public MusicService getService() {
            return MusicService.this;
        }

    }

    void start() {
        mediaPlayer.start();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    void stop() {
        mediaPlayer.stop();
    }

    void release() {
        mediaPlayer.release();
    }

    int getDuration() {
        return mediaPlayer.getDuration();
    }

    void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }
    
    void createMediaPlayer(int positionInner)
    {
        position = positionInner;
        uri = Uri.parse(musicFiles.get(position).getPath());
        //store the path inside the sharedpreferences
        SharedPreferences.Editor editor  = getSharedPreferences(MUSIC_LAST_PLAYED,MODE_PRIVATE).edit();
        editor.putString(MUSIC_FILE,uri.toString());
        editor.putString(ARTIST_NAME,musicFiles.get(position).getArtist());
        editor.putString(SONG_NAME,musicFiles.get(position).getTitle());
        editor.apply();

        mediaPlayer = MediaPlayer.create(getBaseContext(),uri);
    }

    int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    void pause() {
        mediaPlayer.pause();
    }

    void onCompleted()
    {
        mediaPlayer.setOnCompletionListener(this);

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        if(actionPlaying != null){

            actionPlaying.nextBtnClicked();

            if(mediaPlayer != null)
            {
                createMediaPlayer(position);
                mediaPlayer.start();
                onCompleted();
            }
        }


    }

    void setCallBack(ActionPlaying actionPlaying)
    {
        this.actionPlaying = actionPlaying;
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
                .setAction(ACTION_NEXT);
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
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

//        NotificationManager notificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(0,notification);

        //implement foreground, when app remove from recent
        // activity but app still works at the foreground

        startForeground(1,notification);



    }

    private  byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return  art;
    }

    public void nextBtnClicked(){
        if(actionPlaying != null)
        {
            Log.e("Inside", "Action");
            actionPlaying.nextBtnClicked();
        }
    }

    public void previousBtnClicked(){
        if(actionPlaying != null)
        {
            Log.e("Inside", "Action");
            actionPlaying.prevBtnClicked();
        }
    }

    public void playPauseBtnClicked(){

        if(actionPlaying != null)
        {
            Log.e("Inside", "Action");
            actionPlaying.playPauseBtnClicked();
        }
    }
}
