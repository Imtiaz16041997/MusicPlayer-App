package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.musicplayer.Models.MusicFiles;

import java.util.ArrayList;

public class MusicService extends Service {

    IBinder mBinder= new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles>  musicFiles = new ArrayList<>();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public  class MyBinder extends Binder {

        MusicService getService (){
            return MusicService.this;
        }

    }



}
