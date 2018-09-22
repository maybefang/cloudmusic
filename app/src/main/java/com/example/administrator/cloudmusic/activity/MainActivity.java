package com.example.administrator.cloudmusic.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.administrator.cloudmusic.R;
import com.example.administrator.cloudmusic.pager.BasePage;
import com.example.administrator.cloudmusic.pager.LocalMusicPager;
import com.example.administrator.cloudmusic.pager.NetMusicPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOCAL_MUSIC_LIST = 0;
    private static final int NET_FIND = 1;
    private static int position;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private static List<BasePage> fragments = new ArrayList<>();

    private void initView() {
        navigationView = findViewById(R.id.music_navigation_view);
        toolbar = findViewById(R.id.music_toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.drawer_option);
        }
        navigationView.setCheckedItem(R.id.local_music);
        setFragment();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    default:
                        break;
                    case R.id.local_music:
                        //加载本地曲目
                        position = LOCAL_MUSIC_LIST;
                        break;
                    case R.id.search_in_net:
                        //管乐网搜歌
                        position = NET_FIND;
                        break;
                }
                setFragment();
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void setFragment(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_pager,new MyFragment());
        transaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionApply();
        initData();
        initView();
    }

    private void permissionApply() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 0:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "请授权，否则某些功能将不可用", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "请授权，否则某些功能将不可用", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initData() {
        fragments.add(new LocalMusicPager(MainActivity.this));
        fragments.add(new NetMusicPager(MainActivity.this));
    }

    @SuppressLint("ValidFragment")
    public static class MyFragment extends Fragment{
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            BasePage basePage = getPager();
            if (basePage != null) {
                return basePage.view;
            }
            return null;
        }
    }

    private static BasePage getPager() {
        BasePage basePage = fragments.get(position);
        if (!basePage.isInitData){
            basePage.initData();
            basePage.isInitData = true;
        }
        return basePage;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toobar_menu,menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        if (!fragments.isEmpty()){
            fragments.clear();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            default:
                break;
            case R.id.back_up:
                finish();
                break;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }
}
