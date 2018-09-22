package com.example.administrator.cloudmusic.pager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.cloudmusic.R;
import com.example.administrator.cloudmusic.activity.MusicListCreate;
import com.example.administrator.cloudmusic.adapter.MusicListAdapter;
import com.example.administrator.cloudmusic.domain.MusicListItem;

import java.util.ArrayList;
import java.util.List;

public class LocalMusicPager extends BasePage {
    private static final int FIRST_REFRESH = 0;
    private static final int REFRESH_AGAIN = 1;
    private MusicListAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton add_music_list;
    private List<MusicListItem> musicListItems = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;
    public LocalMusicPager(Context context) {
        super(context);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case FIRST_REFRESH:
                    GridLayoutManager manager = new GridLayoutManager(context,2);
                    adapter = new MusicListAdapter(musicListItems);
                    recyclerView.setLayoutManager(manager);
                    recyclerView.setAdapter(adapter);
                    handler.removeMessages(FIRST_REFRESH);
                    break;
                case REFRESH_AGAIN:
                    Toast.makeText(context, "刷新完成", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                    handler.removeMessages(REFRESH_AGAIN);
                    break;
            }

        }
    };

    @Override
    public void initData() {
        getDataFromLocal();
    }

    private void getDataFromLocal() {
        for (int i = 0; i < 3; i++) {
            MusicListItem musicListItem = new MusicListItem();
            switch (i){
                default:
                    break;
                case 0:
                    musicListItem.setMusic_list_name("本地资源");
                    musicListItem.setImage_id(R.drawable.app_start);
                    break;
                case 1:
                    musicListItem.setMusic_list_name("乐团金曲");
                    musicListItem.setImage_id(R.drawable.net_resource);
                    break;
                case 2:
                    musicListItem.setMusic_list_name("管乐网本地资源");
                    musicListItem.setImage_id(R.drawable.cnbrass);
                    break;

            }
            musicListItems.add(musicListItem);
        }
        handler.sendEmptyMessage(FIRST_REFRESH);
    }

    @Override
    public View initView() {
        @SuppressLint("InflateParams") View view1 = LayoutInflater.from(context).inflate(R.layout.local_music_pager,null);
        recyclerView = view1.findViewById(R.id.local_music_list_recycler);
        add_music_list = view1.findViewById(R.id.add_music_list);
        refreshLayout = view1.findViewById(R.id.music_list_fresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessage(REFRESH_AGAIN);
            }
        });
        add_music_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view,"你要创建新的歌单吗",Snackbar.LENGTH_SHORT).setAction("是的", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, MusicListCreate.class);
                        context.startActivity(intent);
                    }
                }).show();
            }
        });
        return view1;
    }
}
