package com.example.musicplayer.AlbumAdapter;

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
import com.example.musicplayer.AlbumDetails;
import com.example.musicplayer.Models.MusicFiles;
import com.example.musicplayer.PlayerActivity;
import com.example.musicplayer.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class AlbumAdapter extends RecyclerView.Adapter<MyAlbumViewHolder> {

    private Context mContext;
    private ArrayList<MusicFiles> albumFiles;
    View view;


    public AlbumAdapter(Context mContext, ArrayList<MusicFiles> albumFiles) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public MyAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_item,parent,false);
        return new MyAlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAlbumViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.album_name.setText(albumFiles.get(position).getAlbum());
        byte[] image = getAlbumArt(albumFiles.get(position).getPath());

        if(image != null)
        {
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_image);
        }
        else
        {
            Glide.with(mContext)
                    .load(R.drawable.m2)
                    .into(holder.album_image);
        }
        //album details
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AlbumDetails.class);
                //when click songs it will be playing
                intent.putExtra("albumName",albumFiles.get(position).getAlbum());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
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
