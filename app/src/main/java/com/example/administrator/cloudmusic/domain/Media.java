package com.example.administrator.cloudmusic.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Media implements Parcelable {
    private long duration;
    private long size;
    private String artist;
    private String mediaName = "";
    private String data;

    public Media(){};

    public Media(Parcel in) {
        duration = in.readLong();
        size = in.readLong();
        artist = in.readString();
        mediaName = in.readString();
        data = in.readString();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getMediaName() {
        mediaName = mediaName.split(".mp3")[0];
        String[] strings = mediaName.split("-");
        if (strings.length >= 2){
            mediaName = strings[1];
        }
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Media{" +
                "duration=" + duration +
                ", size=" + size +
                ", artist='" + artist + '\'' +
                ", mediaName='" + mediaName + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(duration);
        parcel.writeLong(size);
        parcel.writeString(artist);
        parcel.writeString(mediaName);
        parcel.writeString(data);
    }
}
