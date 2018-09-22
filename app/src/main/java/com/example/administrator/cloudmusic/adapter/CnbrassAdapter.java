package com.example.administrator.cloudmusic.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.cloudmusic.R;
import com.example.administrator.cloudmusic.activity.MusicPlayer;
import com.example.administrator.cloudmusic.domain.ItemCnbrass;
import com.example.administrator.cloudmusic.domain.Media;
import com.example.administrator.cloudmusic.utils.DownloadUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CnbrassAdapter extends RecyclerView.Adapter<CnbrassAdapter.ViewHolder> {
    private static final int DOWNLOAD = 1;
    private static final int ERRORDOWNLOAD = 2;
    private List<ItemCnbrass> cnbrasses;
    private Context context;
    private boolean isLongClick = false;

    public CnbrassAdapter(List<ItemCnbrass> cnbrasses){
        this.cnbrasses = cnbrasses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.cnbrass_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final int position = viewHolder.getAdapterPosition();
                final ItemCnbrass itemCnbrass = cnbrasses.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("你的选择");
                builder.setMessage("请选择下一步操作");
                builder.setCancelable(false);
                builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewHolder.progressBar.setVisibility(View.VISIBLE);
                        download(itemCnbrass,viewHolder);
                    }
                });
                builder.setNegativeButton("试听", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForPlay(context,itemCnbrass);
                    }
                });
                builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "您已取消操作", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                viewHolder.textView.setSelected(isLongClick);
                isLongClick = !isLongClick;
                return true;
            }
        });
        return viewHolder;
    }

    private void startActivityForPlay(Context context1, ItemCnbrass cnbrass) {
        List<Media> mediaList = new ArrayList<>();
        Media media = new Media();
        media.setData(cnbrass.getDataUrl());
        media.setMediaName(cnbrass.getSrcName());
        media.setArtist("管乐网试听");
        mediaList.add(media);
        Intent intent = new Intent(context1, MusicPlayer.class);
        intent.putParcelableArrayListExtra("musiclist", (ArrayList<? extends Parcelable>) mediaList);
        intent.putExtra("position",0);
        intent.putExtra("list_cover",R.drawable.app_start);
        context1.startActivity(intent);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DOWNLOAD:
                    Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
                    handler.removeMessages(DOWNLOAD);
                    break;
                case ERRORDOWNLOAD:
                    Toast.makeText(context, "出现不明原因，下载失败", Toast.LENGTH_SHORT).show();
                    handler.removeMessages(ERRORDOWNLOAD);
                    break;
            }
        }
    };

    private void download(ItemCnbrass itemCnbrass, final ViewHolder viewHolder) {
        String name = itemCnbrass.getSrcName();
        if (name.contains("/")){
            String[] names = name.split("/");
            name = names[names.length - 1] + ".mp3";
        }
        DownloadUtil.get().download(itemCnbrass.getDataUrl(), "cnbrass",name, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                handler.sendEmptyMessage(DOWNLOAD);
            }

            @Override
            public void onDownloading(int progress) {
                viewHolder.progressBar.setProgress(progress);
            }

            @Override
            public void onDownloadFailed() {
                handler.sendEmptyMessage(ERRORDOWNLOAD);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemCnbrass cnbrass = cnbrasses.get(position);
        holder.textView.setText(cnbrass.getSrcName());
        Glide.with(context).load(cnbrass.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return cnbrasses.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        CardView cardView;
        TextView textView;
        CircleImageView imageView;
        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cnbrass_mes_card);
            progressBar = itemView.findViewById(R.id.download);
            textView = itemView.findViewById(R.id.cnbrass_item_name);
            imageView = itemView.findViewById(R.id.cnbrass_item_image);
        }
    }
}
