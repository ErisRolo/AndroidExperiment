package com.example.guohouxiao.mp3demo.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.guohouxiao.mp3demo.R;
import com.example.guohouxiao.mp3demo.adapter.MusicAdapter;
import com.example.guohouxiao.mp3demo.bean.Music;
import com.example.guohouxiao.mp3demo.utils.MusicUtils;
import com.example.guohouxiao.mp3demo.utils.ShowToast;
import com.example.guohouxiao.mp3demo.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView rv_music;
    private LinearLayoutManager mLayoutManager;
    private MusicAdapter mAdapter;
    private List<Music> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {

        StatusBarUtils.FlymeSetStatusBarLightMode(this.getWindow(), true);

        mSwipeRefreshLayout = findViewById(R.id.mSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        rv_music = findViewById(R.id.rv_music);
        mLayoutManager = new LinearLayoutManager(this);
        rv_music.setLayoutManager(mLayoutManager);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        ShowToast.showShortToast(MainActivity.this, "已获取到本地音乐！");
                        mList.addAll(MusicUtils.findMusic(MainActivity.this));
                        mAdapter = new MusicAdapter(MainActivity.this, mList);
                        rv_music.setAdapter(mAdapter);
                    }
                }, 1000);
            }
        });
    }


    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mList.clear();
                mList.addAll(MusicUtils.findMusic(MainActivity.this));
                mAdapter.notifyDataSetChanged();

                mSwipeRefreshLayout.setRefreshing(false);
                ShowToast.showShortToast(MainActivity.this, "刷新成功！");
            }
        }, 1000);

    }

}
