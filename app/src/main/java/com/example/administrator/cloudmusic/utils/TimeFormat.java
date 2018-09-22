package com.example.administrator.cloudmusic.utils;

public class TimeFormat {
    public static String toTime(long length){
        long sec = length/1000;
        int hour = (int) (sec / 3600);
        int minute = (int) ((sec - hour * 3600) / 60);
        int seco = (int) (sec % 60);
        String time = "";
        if (hour < 10){
            time += "0"+ hour + ":";
        } else {
            time += hour + ":";
        }
        if (minute < 10){
            time += "0"+ minute + ":";
        } else {
            time += minute + ":";
        }
        if (seco < 10){
            time += "0"+ seco;
        } else {
            time += seco;
        }

        return time;
    }
    public static String toTime2(long length){
        long sec = length/1000;
        int minute = (int) (sec / 60);
        int seco = (int) (sec % 60);
        String time = "";
        if (minute < 10){
            time += "0"+ minute + ":";
        } else {
            time += minute + ":";
        }
        if (seco < 10){
            time += "0"+ seco;
        } else {
            time += seco;
        }

        return time;
    }
}
