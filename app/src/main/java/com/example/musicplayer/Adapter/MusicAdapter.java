package com.example.musicplayer.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.Models.MusicFiles;
import com.example.musicplayer.PlayerActivity;
import com.example.musicplayer.R;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private Context mContext;
    private ArrayList<MusicFiles> mFiles;


    public MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles) {
        this.mContext = mContext;
        this.mFiles = mFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.file_name.setText(mFiles.get(position).getTitle());
        byte[] image = getAlbumArt(mFiles.get(position).getPath());

        if(image != null)
        {
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_art);
        }
        else
        {
            Glide.with(mContext)
                    .load(R.drawable.m2)
                    .into(holder.album_art);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                //when click songs it will be playing
                intent.putExtra("position",position);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }


    //get the image
    private  byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return  art;
    }
}
