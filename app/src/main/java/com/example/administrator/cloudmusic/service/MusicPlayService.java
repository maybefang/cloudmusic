package com.example.administrator.cloudmusic.service;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.RemoteViews;

import com.example.administrator.cloudmusic.IMusicPlayService;
import com.example.administrator.cloudmusic.R;
import com.example.administrator.cloudmusic.activity.MusicPlayer;
import com.example.administrator.cloudmusic.domain.BusMessage;
import com.example.administrator.cloudmusic.domain.Media;
import com.example.administrator.cloudmusic.utils.CacheUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.app.Notification.VISIBILITY_SECRET;

public class MusicPlayService extends Service {
    private static final int DEFAULT_PLAY_MODE = 1;
    private static final int SINGLE_LOOP_PLAY = 2;
    private static final int ALL_LOOP_PLAY = 3;
    private ArrayList<Media> mediaList;
    private int position;
    private int playMode;
    private boolean isNowPlaying = true;
    private NotificationManager manager;
    private ObjectAnimator animator;

    /**
     * 当前播放的音频
     */
    private Media media;

    /**
     * 播放器
     */
    private MediaPlayer mediaPlayer;
//    private int position;

    public MusicPlayService() {
    }

    private IMusicPlayService.Stub stub = new IMusicPlayService.Stub() {
        MusicPlayService service = MusicPlayService.this;

        @Override
        public void setIsNowPlaying(boolean isNowPlaying){
            service.setIsNowPlaying(isNowPlaying);
        }

        @Override
        public void setPosition(int position){
            service.setPosition(position);
        }

        @Override
        public boolean isNowPlaying(){
            return service.isNowPlaying();
        }

        @Override
        public void open() throws RemoteException {
            service.open();
        }

        @Override
        public void setMediaList(List<Media> mediaList) throws RemoteException {
            service.setMediaList(mediaList);
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void resume() throws RemoteException {
            service.resume();
        }

        @Override
        public void setPlayMode(int nowMode) throws RemoteException {
            service.setPlayMode(nowMode);
        }

        @Override
        public void getPlayMode() throws RemoteException {
            service.getPlayMode();
        }

        @Override
        public void startNext() throws RemoteException {
            service.startNext();
        }

        @Override
        public void seekTo(int playPosition) throws RemoteException {
            service.seekTo(playPosition);
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public long getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String coverLocation() throws RemoteException {
            return service.coverLocation();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getMusicName() throws RemoteException {
            return service.getMusicName();
        }

        @Override
        public void startPre() throws RemoteException {
            service.startPre();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return stub;
    }

    /**
     * 根据位置打开音频
     */
    private void open(){
        if (mediaList != null && mediaList.size() > 0) {
            media = mediaList.get(position);
        }
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(media.getData());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    BusMessage message = new BusMessage();
                    message.setMusicName(media.getMediaName());
                    message.setMusicArtist(media.getArtist());
                    message.setMusicDuration(mediaPlayer.getDuration());
                    EventBus.getDefault().post(message);
                    resume();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (playMode == SINGLE_LOOP_PLAY){
                        open();
                    }else if (playMode == ALL_LOOP_PLAY){
                        startNext();
                    }else if (playMode == DEFAULT_PLAY_MODE){
                        Random random = new Random();
                        position = random.nextInt(mediaList.size());
                        open();
                    }
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    startNext();
                    return false;
                }
            });
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止音乐
     */
    private void stop(){

    }

    /**
     * 设置拖动
     * @param playPosition 拖动进度
     */
    private void seekTo(int playPosition){
        mediaPlayer.seekTo(playPosition);
    }

    /**
     * 暂停
     */
    private void pause(){
        mediaPlayer.pause();
        manager.cancel(1);
    }

    /**
     * 继续播放
     */
    private void resume(){
        if (isNowPlaying){
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(this, MusicPlayer.class);
            intent.putExtra("notification",true);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = null;
            RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notification);
            remoteViews.setTextViewText(R.id.music_name_notification,this.getMusicName());
//            PendingIntent pendingIntent1 = new PendingIntent();
//            remoteViews.setOnClickPendingIntent(R.id.play_after_notification,);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel("channel_id","channel_name",NotificationManager.IMPORTANCE_DEFAULT);
                channel.canBypassDnd();
                channel.enableLights(false);
                channel.setLockscreenVisibility(VISIBILITY_SECRET);
                channel.enableVibration(false);
                channel.setBypassDnd(true);
                manager.createNotificationChannel(channel);
                notification = new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setChannelId("channel_id")
                        .setContentIntent(pendingIntent)
                        .setStyle(new Notification.MediaStyle())
                        .setAutoCancel(false)
                        .setContentTitle("云音乐播放中....")
                        .setContentText(this.getMusicName())
//                        .setCustomBigContentView(remoteViews)
                        .setOngoing(true)
                        .build();
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notification = new Notification.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(false)
                            .setOngoing(true)
                            .setContentTitle("云音乐播放中....")
                            .setContentText(this.getMusicName())
                            .build();
//                    notification.bigContentView = remoteViews;
//                    notification.contentView = remoteViews;
                }
            }
            manager.notify(1,notification);
            CacheUtils.putString(MusicPlayService.this,"position",position);
            mediaPlayer.start();
        }
    }

    /**
     * 设置播放模式
     * @param nowMode 当前播放模式
     */
    private void setPlayMode(int nowMode){
        this.playMode = nowMode;
    }

    /**
     * 获取播放模式
     */
    private void getPlayMode(){
    }

    /**
     * 获取当前播放状态
     * @return 当前播放状态
     */
    private boolean isNowPlaying(){
        return mediaPlayer.isPlaying();
    }

    /**
     * 设置传递过来的是否播放的消息
     * @param isNowPlaying 传递的判定值
     */
    private void setIsNowPlaying(boolean isNowPlaying){
        this.isNowPlaying = isNowPlaying;
    }

    /**
     * 播放下一曲
     */
    private void startNext(){
        if (position < mediaList.size()-1){
            position++;
        }else {
            position = 0;
        }
        open();
    }

    private void setPosition(int position){
        this.position = position;
    }

    /**
     * 当前播放进度
     * @return 当前进度
     */
    private int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 获取当前时长
     * @return 当前时长
     */
    private long getDuration(){
        return mediaPlayer.getDuration();
    }

    /**
     * 获取封面位置
     * @return 封面位置
     */
    private String coverLocation(){
        return null;
    }

    /**
     * 获取当前艺术家
     * @return 艺术家
     */
    private String getArtist(){
        return media.getArtist();
    }

    /**
     * 设置播放列表
     */
    private void setMediaList(List<Media> mediaList){
        this.mediaList = (ArrayList<Media>) mediaList;
    }

    /**
     * 获取歌曲名称
     * @return 歌曲名称
     */
    private String getMusicName(){
        return media.getMediaName();
    }

    /**
     * 播放上一曲
     */
    private void startPre(){
        if (position > 0){
            position--;
        }else {
            position = mediaList.size()-1;
        }
        open();
    }
}
