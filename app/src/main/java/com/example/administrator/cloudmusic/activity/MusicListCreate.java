package com.example.administrator.cloudmusic.activity;

import android.annotation.SuppressLint;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.cloudmusic.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MusicListCreate extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextInputLayout newMusicListNameLayout;
    private EditText newMusicListName;
    private TextInputLayout newMusicAuthorNameLayout;
    private EditText newMusicAuthorName;
    private TextInputLayout newMusicCoverLayout;
    private EditText newMusicCover;
    private TextInputLayout newMusicDateLayout;
    private EditText newMusicDate;
    private Button autoGenerate;
    private Button newMusicListSave;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-20 17:04:05 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        newMusicListNameLayout = (TextInputLayout)findViewById( R.id.new_music_list_name_layout );
        newMusicListName = (EditText)findViewById( R.id.new_music_list_name );
        newMusicAuthorNameLayout = (TextInputLayout)findViewById( R.id.new_music_author_name_layout );
        newMusicAuthorName = (EditText)findViewById( R.id.new_music_author_name );
        newMusicCoverLayout = (TextInputLayout)findViewById( R.id.new_music_cover_layout );
        newMusicCover = (EditText)findViewById( R.id.new_music_cover );
        newMusicDateLayout = (TextInputLayout)findViewById( R.id.new_music_date_layout );
        newMusicDate = (EditText)findViewById( R.id.new_music_date );
        autoGenerate = (Button)findViewById( R.id.auto_generate );
        newMusicListSave = (Button)findViewById( R.id.new_music_list_save );
        autoGenerate.setOnClickListener( this );
        newMusicListSave.setOnClickListener( this );
        setErrorListener();
    }

    private void setErrorListener() {
        newMusicDate.addTextChangedListener(new MyTextWatcher(newMusicDate,newMusicDateLayout));
        newMusicAuthorName.addTextChangedListener(new MyTextWatcher(newMusicAuthorName,newMusicAuthorNameLayout));
        newMusicCover.addTextChangedListener(new MyTextWatcher(newMusicCover,newMusicCoverLayout));
        newMusicListName.addTextChangedListener(new MyTextWatcher(newMusicListName,newMusicListNameLayout));

    }

    class MyTextWatcher implements TextWatcher {

        private EditText editText;
        private TextInputLayout textLayout;

        MyTextWatcher(EditText editText,TextInputLayout textInputLayout){
            this.editText = editText;
            this.textLayout = textInputLayout;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editText.getText() == null || editText.getText().toString().trim().equals("")) {
                textLayout.setError("不能为空");
            }else {
                textLayout.setError(null);
            }
        }
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-09-20 17:04:05 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == autoGenerate ) {
            // Handle clicks for autoGenerate;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String string = format.format(new Date());
            newMusicDate.setText(string);
        } else if ( v == newMusicListSave ) {
            // Handle clicks for newMusicListSave
            if (newMusicListName.getText() == null || newMusicListName.getText().toString().trim().equals("")){
                newMusicListNameLayout.setError("歌单名不能为空");
            }else if (newMusicAuthorName.getText() == null || newMusicAuthorName.getText().toString().trim().equals("")){
                newMusicAuthorNameLayout.setError("创作者不能为空");
            }else if (newMusicCover.getText() == null || newMusicCover.getText().toString().trim().equals("")){
                newMusicCoverLayout.setError("封面不能为空");
            }else if (newMusicDate.getText() == null || newMusicDate.getText().toString().trim().equals("")){
                newMusicDateLayout.setError("创作日期不能为空");
            }else {
                Toast.makeText(this, "该功能还在测试中，暂不开放", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list_create);
        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.list_create_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findViews();
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
}
