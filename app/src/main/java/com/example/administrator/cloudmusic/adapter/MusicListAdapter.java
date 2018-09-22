package com.example.administrator.cloudmusic.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.cloudmusic.R;
import com.example.administrator.cloudmusic.activity.MusicListActivity;
import com.example.administrator.cloudmusic.domain.MusicListItem;

import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {
    private List<MusicListItem> musicListItems;
    private Context context;

    public MusicListAdapter(List<MusicListItem> musicListItems) {
        this.musicListItems = musicListItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_list_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开启下一个页面，即音乐列表界面
                startMusicListActivity(context,viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    private void startMusicListActivity(Context nextContext,int position) {
        Intent intent = new Intent(nextContext, MusicListActivity.class);
        MusicListItem musicListItem = musicListItems.get(position);
        intent.putExtra("list_id",position);
        intent.putExtra("list_cover",musicListItem.getImage_id());
        intent.putExtra("list_name",musicListItem.getMusic_list_name());
        nextContext.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //对第i个元素进行初始化
        MusicListItem musicListItem = musicListItems.get(position);
        holder.music_list_name.setText(musicListItem.getMusic_list_name());
        Glide.with(context).load(musicListItem.getImage_id()).into(holder.music_image_id);
    }

    @Override
    public int getItemCount() {
        return musicListItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView music_image_id;
        TextView music_list_name;
        View view;
        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            music_image_id = itemView.findViewById(R.id.music_list_image);
            music_list_name = itemView.findViewById(R.id.music_list_name);
        }
    }
}
