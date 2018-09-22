package com.example.administrator.cloudmusic.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.administrator.cloudmusic.IMusicPlayService;
import com.example.administrator.cloudmusic.R;
import com.example.administrator.cloudmusic.domain.BusMessage;
import com.example.administrator.cloudmusic.domain.Media;
import com.example.administrator.cloudmusic.service.MusicPlayService;
import com.example.administrator.cloudmusic.utils.CacheUtils;
import com.example.administrator.cloudmusic.utils.TimeFormat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class MusicPlayer extends AppCompatActivity implements View.OnClickListener {

    private static final int FLUSH_PROGRESS = 1;
    private int playMode;
    private int position;
    private Media media;
    private List<Media> mediaList;
    private Toolbar music_player_toolbar;
    private TextView toolbar_music_name;
    private TextView toolbar_music_artist;
    private CircleImageView imageView;
    private CoordinatorLayout coordinatorLayout;
    private ObjectAnimator animator;
    private Button play_mode;
    private Button play_pre;
    private Button play_after;
    private Button play_pause;
    private Button last_play_button;
    private TextView music_now_time;
    private TextView music_total_time;
    private boolean isFromWidow = false;
    private IMusicPlayService service;
    private int coverId;
    private boolean isNowPlaying;
    private SeekBar music_play_seekbar;
    private ServiceConnection con = new ServiceConnection() {

        /**
         * 连接成功回调该方法
         * @param componentName
         * @param iBinder
         */
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = IMusicPlayService.Stub.asInterface(iBinder);
            if (service != null) {
                try {
                    service.setMediaList(mediaList);
                    service.setPosition(position);
                    playMode = CacheUtils.getString(MusicPlayer.this,"playMode");
                    service.setPlayMode(playMode);
                    int a = 1;
                    switch (playMode){
                        default:
                            break;
                        case 1:
                            a = R.drawable.random_selector;
                            break;
                        case 2:
                            a = R.drawable.single_loop_selector;
                            break;
                        case 3:
                            a = R.drawable.all_loop_selector;
                            break;
                    }
                    play_mode.setBackgroundResource(a);
                    if (!isFromWidow){
                        try{
                            if (CacheUtils.getString(MusicPlayer.this,"position") == position){
                                service.resume();
                                music_total_time.setText(TimeFormat.toTime2(service.getDuration()));
                                music_play_seekbar.setMax((int) service.getDuration());
                                handler.sendEmptyMessage(FLUSH_PROGRESS);
                                toolbar_music_artist.setText(service.getArtist());
                                toolbar_music_name.setText(service.getMusicName());
                                if (service.isNowPlaying()){
                                    animator.resume();
                                    play_pause.setBackgroundResource(R.drawable.pause_selector);
                                }else {
//                                animator.pause();
                                    play_pause.setBackgroundResource(R.drawable.play_change_selector);
                                }
                            }else {

                                play_pause.setBackgroundResource(R.drawable.pause_selector);
                                service.setPosition(position);
                                service.open();
                            }

                        }catch (Exception e){

                            service.open();
                        }
                    }else {
                        service.resume();
                        music_total_time.setText(TimeFormat.toTime2(service.getDuration()));
                        music_play_seekbar.setMax((int) service.getDuration());
                        handler.sendEmptyMessage(FLUSH_PROGRESS);
                        toolbar_music_artist.setText(service.getArtist());
                        toolbar_music_name.setText(service.getMusicName());
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 连接断开回调该方法
         * @param componentName
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            try {
                if (service != null) {
                    service.stop();
                    service = null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case FLUSH_PROGRESS:
                    try {
                        int currentProgress = service.getCurrentPosition();
                        music_play_seekbar.setProgress(currentProgress);
                        music_now_time.setText(TimeFormat.toTime2(currentProgress));
                        handler.removeMessages(FLUSH_PROGRESS);
                        handler.sendEmptyMessageDelayed(FLUSH_PROGRESS,1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        getDataFromLastActivity();
        initView();
        bindAndStartService();
        initData();
    }

    private void bindAndStartService() {
        Intent intent = new Intent(MusicPlayer.this, MusicPlayService.class);
        intent.setAction("com.example.administrator.cloudmusic_MUSIC_PLAY_SERVICE");
        bindService(intent,con, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        if ( v == play_after ) {
            // Handle clicks for playMode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (service != null) {
                    try {
                        isNowPlaying = service.isNowPlaying();
                        if (isNowPlaying){
                            animator.end();
                            animator.start();
                            service.setIsNowPlaying(true);
                            service.startNext();
                        }else{
                            animator.end();
                            animator.start();
                            animator.pause();
                            service.setIsNowPlaying(false);
                            service.startNext();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if ( v == play_pause ) {
            // Handle clicks for playPre
            if (service != null) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        isNowPlaying = service.isNowPlaying();
                        if (isNowPlaying){
                            animator.pause();
                            service.setIsNowPlaying(false);
                            service.pause();
                            play_pause.setBackgroundResource(R.drawable.play_change_selector);
                        }else{
                            animator.resume();
                            service.setIsNowPlaying(true);
                            service.resume();
                            play_pause.setBackgroundResource(R.drawable.pause_selector);
                        }
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if ( v == play_mode ) {
            // Handle clicks for playPause
            switch (playMode){
                default:
                    break;
                case 1:
                    playMode = 2;
                    play_mode.setBackgroundResource(R.drawable.single_loop_selector);
                    break;
                case 2:
                    playMode = 3;
                    play_mode.setBackgroundResource(R.drawable.all_loop_selector);
                    break;
                case 3:
                    playMode = 1;
                    play_mode.setBackgroundResource(R.drawable.random_selector);
                    break;
            }
            try {
                service.setPlayMode(playMode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            CacheUtils.putString(MusicPlayer.this,"playMode",playMode);
        } else if ( v == play_pre ) {
            // Handle clicks for playAfter
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (service != null) {
                    try {
                        isNowPlaying = service.isNowPlaying();
                        if (isNowPlaying){
                            animator.end();
                            animator.start();
                            service.setIsNowPlaying(true);
                            service.startPre();
                        }else{
                            animator.end();
                            animator.start();
                            animator.pause();
                            service.setIsNowPlaying(false);
                            service.startPre();
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if ( v == last_play_button ) {
            // Handle clicks for lastPlayButton

        }
    }

    private void initView() {
        music_play_seekbar = findViewById(R.id.music_play_seekbar);
        music_now_time = findViewById(R.id.music_now_time);
        music_total_time = findViewById(R.id.music_total_time);
        play_after = findViewById(R.id.play_after);
        play_mode = findViewById(R.id.play_mode);
        play_pause = findViewById(R.id.play_pause);
        play_pre = findViewById(R.id.play_pre);
        last_play_button = findViewById(R.id.last_play_button);
        imageView = findViewById(R.id.play_cover);
        coordinatorLayout = findViewById(R.id.player_layout);
        music_player_toolbar = findViewById(R.id.music_player_toolbar);
        setSupportActionBar(music_player_toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar_music_artist = findViewById(R.id.toolbar_text_artist);
        toolbar_music_name = findViewById(R.id.toolbar_text_name);
        Glide.with(MusicPlayer.this).load(coverId)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25,3)))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            coordinatorLayout.setBackground(drawable);
                        }
                    }
                });
        RequestOptions options = new RequestOptions()
                .error(R.drawable.app_start);
        Glide.with(MusicPlayer.this).load(coverId)
                .apply(options)
                .into(imageView);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        play_after.setOnClickListener(this);
        play_mode.setOnClickListener(this);
        play_pause.setOnClickListener(this);
        play_pre.setOnClickListener(this);
        last_play_button.setOnClickListener(this);
        animator = ObjectAnimator.ofFloat(imageView,"rotation",0.0f,360.0f);
        animator.setDuration(60000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.start();
        play_pause.setBackgroundResource(R.drawable.pause_selector);
        music_play_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                    try {
                        service.seekTo(i);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMessage(BusMessage message){
        toolbar_music_name.setText(message.getMusicName());
        toolbar_music_artist.setText(message.getMusicArtist());
        music_total_time.setText(TimeFormat.toTime2(message.getMusicDuration()));
        music_play_seekbar.setMax((int) message.getMusicDuration());
        handler.sendEmptyMessage(FLUSH_PROGRESS);
    }

    @Override
    protected void onDestroy() {
        if (service != null) {
            service = null;
        }
        handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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

    private void getDataFromLastActivity() {
        Intent intent = getIntent();
        mediaList = intent.getParcelableArrayListExtra("musiclist");
        isFromWidow = intent.getBooleanExtra("notification",false);
        if (!isFromWidow){
            position =  intent.getIntExtra("position",0);
            coverId = intent.getIntExtra("list_cover",R.drawable.app_start);
        }
    }
    private void initData() {
        EventBus.getDefault().register(this);
    }

}
