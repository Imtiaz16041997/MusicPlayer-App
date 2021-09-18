package com.example.musicplayer.Adapter;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.Models.MusicFiles;
import com.example.musicplayer.PlayerActivity;
import com.example.musicplayer.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private Context mContext;
    public static ArrayList<MusicFiles> mFiles;


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
        //menu delete pop up
        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch  (menuItem.getItemId()){

                            case R.id.delete :

                                Toast.makeText(mContext,"Delete Clicked!!",Toast.LENGTH_SHORT).show();

                                deleteFile(position, view);
                                break;
                        }

                        return true;
                    }
                });
            }
        });

    }

    private void deleteFile(int position, View view)
    {
        //we want file delete permanently from storage
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,Long.parseLong(mFiles.get(position).getId()));  //Content

        File file = new File(mFiles.get(position).getPath());
        boolean deleted = file.delete(); // delete your file

        if (deleted)

        {
            mContext.getContentResolver().delete(contentUri, null,null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mFiles.size());

            Snackbar.make(view, "File Deleted : ", Snackbar.LENGTH_LONG).show();
        }

        else

        {
            // may be file in sd card
            Snackbar.make(view, "File Can't be Deleted : ", Snackbar.LENGTH_LONG).show();
        }

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

    //for search
    public void updateList(ArrayList<MusicFiles> musicFilesArrayList)
    {
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
