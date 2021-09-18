package com.example.musicplayer;

import static com.example.musicplayer.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.AlbumAdapter.AlbumDetailsAdapter;
import com.example.musicplayer.Models.MusicFiles;

import java.util.ArrayList;

public class AlbumDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumPhoto;
    String albumName;
    ArrayList<MusicFiles> albumSongs = new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);

        recyclerView = findViewById(R.id.recyclerView);
        albumPhoto = findViewById(R.id.albumPhoto);
        albumName = getIntent().getStringExtra("albumName");
        int j = 0;

        for(int i = 0; i < musicFiles.size(); i++)
        {
            if(albumName.equals(musicFiles.get(i).getAlbum()))

            {
                if(!albumSongs.contains(musicFiles.get(i)))
                {
                    albumSongs.add(j,musicFiles.get(i));
//                    albumName.setText(musicFiles.get(i).getAlbum());
                    j++;

                }

            }
        }

        byte[] image = getAlbumArt(albumSongs.get(0).getPath());
        if (image != null)
        {
            Glide.with(this)
                    .load(image)
                    .into(albumPhoto);
        }

        else

        {
            Glide.with(this)
                    .load(R.drawable.m2)
                    .into(albumPhoto);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(albumSongs.size() < 1))
        {
            albumDetailsAdapter = new AlbumDetailsAdapter(this,albumSongs);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));

        }
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