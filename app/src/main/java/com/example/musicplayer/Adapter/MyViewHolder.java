package com.example.musicplayer.Adapter;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView file_name;
    public ImageView album_art;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        file_name = itemView.findViewById(R.id.music_file_name);
        album_art = itemView.findViewById(R.id.music_img);
    }




}
