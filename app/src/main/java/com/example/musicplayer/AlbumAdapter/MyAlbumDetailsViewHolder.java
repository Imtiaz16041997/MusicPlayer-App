package com.example.musicplayer.AlbumAdapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;

public class MyAlbumDetailsViewHolder  extends RecyclerView.ViewHolder{
    public ImageView album_image;
    public TextView album_name;


    public MyAlbumDetailsViewHolder(@NonNull View itemView) {
        super(itemView);

        album_image = itemView.findViewById(R.id.music_img);
        album_name = itemView.findViewById(R.id.music_file_name);


    }
}
