package com.example.administrator.cloudmusic.pager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.cloudmusic.R;
import com.example.administrator.cloudmusic.adapter.CnbrassAdapter;
import com.example.administrator.cloudmusic.domain.ItemCnbrass;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NetMusicPager extends BasePage {
    private static final int FLUSH = 1;
    private TextInputLayout search_cnbrass_layout;
    private TextInputEditText search_cnbrass;
    private SwipeRefreshLayout cnbrass_find_fresh;
    private RecyclerView cnbrass_find_recycler;
    private Button begin_search;
    private ProgressBar cnbrass_progress_bar;
    private TextView cnbrass_text;
    private String string;
    private CnbrassAdapter adapter;
    private String data;
    private List<ItemCnbrass> cnbrassList = new ArrayList<>();

    public NetMusicPager(Context context) {
        super(context);
    }

    @Override
    public void initData() {

    }

    private void getDataFromCnbrass(final String values) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ItemCnbrass itemCnbrass = new ItemCnbrass();
                Connection connection = Jsoup.connect("http://www.cnbrass.com/search");
                connection.data("keywords",values);
                try {
                    Document document = connection.post();
                    Elements elements1 = document.getElementsByTag("a");
                    for (Element element : elements1) {
                        string = element.absUrl("href");
                        if (string.contains("http://www.cnbrass.com/score/")){
                            String name = element.text();
                            if (name != null && !name.equals("")){
                                itemCnbrass.setSrcName(name);
                                break;
                            }
                        }
                    }
                    Elements elements2 = document.getElementsByTag("img");
                    for (Element element : elements2) {
                        if (!element.absUrl("src").contains("http://f.cnbrass.com/p/image/")){
                            itemCnbrass.setImageUrl(element.absUrl("src"));
                        }
                    }
                    Document document1 = Jsoup.connect(string).get();
                    Elements newHeadlines = document1.getElementsByTag("a");
                    for (Element headline : newHeadlines) {
                        string = headline.absUrl("href");
                        if (string.contains(".mp3")){
                            string = "http://f.cnbrass.com/a/" + string.split("/a/")[1];
                            itemCnbrass.setDataUrl(string);
                        }

                    }
                    if (!cnbrassList.isEmpty()){
                        cnbrassList.clear();
                    }
                    cnbrassList.add(itemCnbrass);
                    handler.sendEmptyMessage(FLUSH);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case FLUSH:
                    GridLayoutManager manager = new GridLayoutManager(context,1);
                    cnbrass_find_recycler.setLayoutManager(manager);
                    adapter = new CnbrassAdapter(cnbrassList);
                    cnbrass_find_recycler.setAdapter(adapter);
                    cnbrass_progress_bar.setVisibility(View.GONE);
                    if (cnbrassList.size() <= 0){
                        cnbrass_text.setText("无搜索结果");
                        cnbrass_text.setTextColor(Color.RED);
                        cnbrass_text.setVisibility(View.VISIBLE);
                    }else {
                        cnbrass_text.setText("共找到" + cnbrassList.size() + "条结果");
                        cnbrass_text.setTextSize(18);
                        cnbrass_text.setVisibility(View.VISIBLE);
                    }
                    handler.removeMessages(FLUSH);
                    break;
            }
        }
    };

    @Override
    public View initView() {
        @SuppressLint("InflateParams") View view1 = LayoutInflater.from(context).inflate(R.layout.cnbrass_pager,null);
        cnbrass_progress_bar = view1.findViewById(R.id.cnbrass_progress_bar);
        cnbrass_text = view1.findViewById(R.id.cnbrass_text);
        cnbrass_find_recycler = view1.findViewById(R.id.cnbrass_find_recycler);
        cnbrass_find_fresh = view1.findViewById(R.id.cnbrass_find_fresh);
        search_cnbrass = view1.findViewById(R.id.search_cnbrass);
        search_cnbrass_layout = view1.findViewById(R.id.search_cnbrass_layout);
        begin_search = view1.findViewById(R.id.begin_search);
        search_cnbrass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (search_cnbrass.getText().toString().trim().equals("")){ search_cnbrass_layout.setError("搜索内容不能为空");
                }else {
                    search_cnbrass_layout.setError(null);
                }
            }
        });
        begin_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data = search_cnbrass.getText().toString().trim();
                if (data.equals("")){
                    search_cnbrass_layout.setError("搜索内容不能为空");
                }else {
                    cnbrass_progress_bar.setVisibility(View.VISIBLE);
                    cnbrass_text.setText(R.string.loading);
                    cnbrass_text.setVisibility(View.VISIBLE);
                    getDataFromCnbrass(data);
                }
            }
        });
        cnbrass_find_fresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                data = search_cnbrass.getText().toString().trim();
                if (data.equals("")){
                    search_cnbrass_layout.setError("搜索内容不能为空");
                }else {
                    if (!cnbrassList.isEmpty()){
                        cnbrassList.clear();
                    }
                    cnbrass_progress_bar.setVisibility(View.VISIBLE);
                    cnbrass_text.setText(R.string.loading);
                    cnbrass_text.setVisibility(View.VISIBLE);
                    getDataFromCnbrass(data);
                    adapter.notifyDataSetChanged();
                }
                cnbrass_find_fresh.setRefreshing(false);

            }
        });

        return view1;
    }
}
