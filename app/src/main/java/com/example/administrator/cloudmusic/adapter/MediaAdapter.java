package com.example.administrator.cloudmusic.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.cloudmusic.R;
import com.example.administrator.cloudmusic.activity.MusicPlayer;
import com.example.administrator.cloudmusic.domain.Media;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private List<Media> mediaList;
    private Context context;
    private boolean isLongClick = true;
    private int imageId;
    public MediaAdapter(List<Media> mediaList, int imageId){
        this.mediaList = mediaList;
        this.imageId = imageId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.music_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                startActivityForPlay(context,position);
            }
        });

        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                viewHolder.musicName.setSelected(isLongClick);
                isLongClick = !isLongClick;
                return true;
            }
        });
        return viewHolder;
    }

    private void startActivityForPlay(Context context1, int position) {
        Intent intent = new Intent(context1, MusicPlayer.class);
        intent.putParcelableArrayListExtra("musiclist", (ArrayList<? extends Parcelable>) mediaList);
        intent.putExtra("position",position);
        intent.putExtra("list_cover",imageId);
        context1.startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Media media = mediaList.get(position);
        holder.musicName.setText(media.getMediaName());
        holder.musicArtist.setText(media.getArtist());
        holder.musicId.setText(position + 1 + "");
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView musicArtist;
        TextView musicName;
        TextView musicId;
        CardView cardView;
        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.music_item_card);
            musicId = itemView.findViewById(R.id.music_id);
            musicName = itemView.findViewById(R.id.music_name);
            musicArtist = itemView.findViewById(R.id.music_artist);
        }
    }
}
