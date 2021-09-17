package com.example.musicplayer.AlbumAdapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;

public class MyAlbumViewHolder  extends RecyclerView.ViewHolder{
    public ImageView album_image;
    public TextView album_name;


    public MyAlbumViewHolder(@NonNull View itemView) {
        super(itemView);

        album_image = itemView.findViewById(R.id.album_image);
        album_name = itemView.findViewById(R.id.album_name);


    }
}
