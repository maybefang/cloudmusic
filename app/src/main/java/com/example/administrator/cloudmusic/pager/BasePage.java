package com.example.administrator.cloudmusic.pager;

import android.content.Context;
import android.view.View;

public abstract class BasePage {
    public Context context;
    public View view;
    public boolean isInitData = false;

    public BasePage(Context context) {
        this.context = context;
        view = initView();
    }

    public abstract void initData();

    public abstract View initView();

}
