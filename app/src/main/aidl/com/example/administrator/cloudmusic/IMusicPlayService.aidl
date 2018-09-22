// IMusicPlayService.aidl
package com.example.administrator.cloudmusic;

import com.example.administrator.cloudmusic.domain.Media;

// Declare any non-default types here with import statements

interface IMusicPlayService {
        /**
         * 根据位置打开音频
         * @param position 位置
         */
        void open();

        void setPosition(int position);

        /**
         * 获取播放列表
         * @return 播放列表
         */
        void setMediaList(in List<Media> mediaList);
        /**
         * 暂停
         */
        void pause();
        /**
         * 继续播放
         */
        void resume();
        /**
         * 获取当前播放状态
         * @return 当前播放状态
         */
        boolean isNowPlaying();
        /**
         * 设置传递过来的是否播放的消息
         * @param isNowPlaying 传递的判定值
         */
         void setIsNowPlaying(boolean isNowPlaying);
        /**
         * 设置播放模式
         * @param nowMode 当前播放模式
         */
        void setPlayMode(int nowMode);

        /**
         * 获取播放模式
         */
        void getPlayMode();
        /**
         * 播放下一曲
         * @param position 当前位置
         * @param nowMode 当前模式
         */
        void startNext();


        /**
        * 设置拖动功能
        * */
        void seekTo(int position);

        /**
         * 当前播放进度
         * @return 当前进度
         */
        int getCurrentPosition();
        /**
         * 获取当前时长
         * @return 当前时长
         */
        long getDuration();

        /**
         * 获取封面位置
         * @return 封面位置
         */
        String coverLocation();

        /**
         * 获取当前艺术家
         * @return 艺术家
         */
        String getArtist();

        /**
         * 获取歌曲地址
         * @return 歌曲地址
         */
        String getMusicName();

        /**
         * 播放上一曲
         * @param position 当前位置
         * @param nowMode 当前模式
         */
        void startPre();
        /**
         * 停止音乐
         */
        void stop();
}
