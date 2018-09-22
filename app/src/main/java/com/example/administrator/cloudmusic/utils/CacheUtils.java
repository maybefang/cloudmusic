package com.example.administrator.cloudmusic.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheUtils {

    public static void putString(Context context,String key,int values){
        SharedPreferences.Editor editor = context.getSharedPreferences("animator",Context.MODE_PRIVATE).edit();
        editor.putInt(key,values);
        editor.apply();
    }

    public static int getString(Context context,String key){
        SharedPreferences preferences= context.getSharedPreferences("animator",Context.MODE_PRIVATE);
        return preferences.getInt(key,1);
    }

}
