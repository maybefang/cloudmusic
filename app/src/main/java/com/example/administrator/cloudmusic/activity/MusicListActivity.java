package com.example.administrator.cloudmusic.activity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.cloudmusic.R;
import com.example.administrator.cloudmusic.adapter.MediaAdapter;
import com.example.administrator.cloudmusic.domain.Media;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class MusicListActivity extends AppCompatActivity {

    private RecyclerView musics;
    private List<Media> mediaList = new ArrayList<>();
    private ImageView imageView;
    private ProgressBar layout;
    private int imageId;
    private SwipeRefreshLayout refreshLayout;
    private String musicList_name;
    private MediaAdapter adapter;
    private TextView music;
    private CollapsingToolbarLayout music_list_cover_bar;
    private int id;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        Intent intent = getIntent();
        id = intent.getIntExtra("list_id",0);
        imageId = intent.getIntExtra("list_cover",R.drawable.app_start);
        musicList_name = intent.getStringExtra("list_name");
        initView();
        initData();
    }
    private void initData() {
        switch (id){
            default:
                break;
            case 0:
                getDataFromLocal();
                break;
            case 1:
                getDataFromNet();
                break;
            case 2:
                getDataFromCnbrass();
                break;
        }
    }

    private void getDataFromCnbrass() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] msgs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//曲名
                        MediaStore.Audio.Media.DURATION,//时长
                        MediaStore.Audio.Media.SIZE,//大小
                        MediaStore.Audio.Media.DATA,//路径
                        MediaStore.Audio.Media.ARTIST,//歌手
                };
                Cursor cursor = resolver.query(uri,msgs,null,null,null);
                if (cursor != null) {
                    while (cursor.moveToNext()){
                        Media media = new Media();
                        String name = cursor.getString(0);
                        long duration = cursor.getLong(1);
                        long size = cursor.getLong(2);
                        String data = cursor.getString(3);
                        String artist = cursor.getString(4);
                        if (data.contains("cnbrass")){
                            media.setArtist(artist);
                            media.setMediaName(name);
                            media.setData(data);
                            media.setDuration(duration);
                            media.setSize(size);
                            mediaList.add(media);
                        }
                    }
                    cursor.close();
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams("http://musicplayed-musiclist.stor.sinaapp.com/musiclist/music.html");
//        RequestParams params = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                System.out.println(cex);
            }

            @Override
            public void onFinished() {
                System.out.println("finish");
            }
        });
    }

    private void processData(String result) {
//        mediaItems = parseJson(result);
        mediaList = parseString(result);
        handler.sendEmptyMessage(0);
    }

    private ArrayList<Media> parseString(String result) {
        ArrayList<Media> mediaItemArrayList = new ArrayList<>();
        String[] music = result.split(";");
        for (String aMusic : music) {
            if (aMusic != null && !aMusic.equals("")) {
                Media mediaItem = new Media();
                String[] url = aMusic.split(",");

                if (url.length > 0) {
                    mediaItem.setData(url[0]);
                    mediaItem.setMediaName(url[1]);
                    mediaItem.setArtist(url[2]);
                    mediaItemArrayList.add(mediaItem);
                }
            }
        }
        return mediaItemArrayList;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter = new MediaAdapter(mediaList,imageId);
            GridLayoutManager manager = new GridLayoutManager(MusicListActivity.this,1);
            musics.setLayoutManager(manager);
            musics.setAdapter(adapter);
            layout.setVisibility(View.GONE);
            if (mediaList.size() == 0){
                music.setText("无音频，请检测是否开启权限，或者是否存在音频");
                music.setTextSize(22);
                music.setVisibility(View.VISIBLE);
            }else {
                music.setVisibility(View.GONE);
            }
        }
    };

    private void getDataFromLocal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] msgs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//曲名
                        MediaStore.Audio.Media.DURATION,//时长
                        MediaStore.Audio.Media.SIZE,//大小
                        MediaStore.Audio.Media.DATA,//路径
                        MediaStore.Audio.Media.ARTIST,//歌手
                };
                Cursor cursor = resolver.query(uri,msgs,null,null,null);
                if (cursor != null) {
                    while (cursor.moveToNext()){
                        Media media = new Media();
                        String name = cursor.getString(0);
                        long duration = cursor.getLong(1);
                        long size = cursor.getLong(2);
                        String data = cursor.getString(3);
                        String artist = cursor.getString(4);
                        media.setArtist(artist);
                        media.setMediaName(name);
                        media.setData(data);
                        media.setDuration(duration);
                        media.setSize(size);
                        mediaList.add(media);
                    }
                    cursor.close();
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        refreshLayout = findViewById(R.id.flush_music_list);
        layout = findViewById(R.id.loading);
        music = findViewById(R.id.loading_music);
        imageView = findViewById(R.id.music_cover);
        musics = findViewById(R.id.musics_recycler);
        imageView.setImageResource(imageId);
        music_list_cover_bar = findViewById(R.id.music_list_cover_bar);
        toolbar = findViewById(R.id.music_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        music_list_cover_bar.setTitle(musicList_name);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
    }
}
