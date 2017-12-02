package com.example.guohouxiao.mp3demo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.guohouxiao.mp3demo.R;
import com.example.guohouxiao.mp3demo.bean.Music;
import com.example.guohouxiao.mp3demo.service.PlayerService;
import com.example.guohouxiao.mp3demo.utils.Config;
import com.example.guohouxiao.mp3demo.utils.MusicUtils;
import com.example.guohouxiao.mp3demo.utils.ShowToast;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by guohouxiao on 2017/11/25.
 * 播放界面
 */

public class PlayActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final String MUSIC_DATA = "music_data";
    private static final String MUSIC_POSITION = "listPosition";
    private Music music;
    private int listPosition;//记录当前音乐在列表中的位置
    private List<Music> mList = new ArrayList<>();

    private Toolbar mToolbar;
    private TextView tv_title;
    private TextView tv_artist;
    private CircleImageView iv_cover;
    private TextView tv_currentTime;
    private SeekBar mSeekBar;
    private TextView tv_totalTime;
    private ImageView iv_previous;
    private ImageView iv_playandpause;
    private ImageView iv_next;

    private Intent service;

    private boolean isFirst = true;
    private boolean isPause = true;
    private int currentTime;//当前播放的时间
    private PlayerReceiver playerReceiver;//用来接收从service传回来的广播
    public static final String MUSIC_CURRENT = "com.example.guohouxiao.MUSIC_CURRENT";//音乐当前时间改变动作
    public static final String MUSIC_FINISH = "com.example.guohouxiao.MUSIC_FINISH";//音乐播放完成时更新动作

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        service = new Intent(this, PlayerService.class);

        music = (Music) getIntent().getSerializableExtra(MUSIC_DATA);
        listPosition = getIntent().getIntExtra(MUSIC_POSITION, 0);
        mList.addAll(MusicUtils.findMusic(this));

        registerReceiver();

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(playerReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {

        mToolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                stopService(service);
            }
        });

        tv_title = findViewById(R.id.tv_title);
        tv_artist = findViewById(R.id.tv_artist);
        iv_cover = findViewById(R.id.iv_cover);
        tv_currentTime = findViewById(R.id.tv_currentTime);
        mSeekBar = findViewById(R.id.mSeekBar);
        tv_totalTime = findViewById(R.id.tv_totalTime);
        iv_previous = findViewById(R.id.iv_previous);
        iv_playandpause = findViewById(R.id.iv_playandpause);
        iv_next = findViewById(R.id.iv_next);

        tv_title.setText(music.getTitle());
        tv_artist.setText(music.getArtist());
        if (music.getCover() != null) {
            Glide.with(this).load(music.getCover()).into(iv_cover);
        }
        mSeekBar.setMax((int) music.getDuration());
        tv_totalTime.setText(MusicUtils.formatTime(music.getDuration()));

        iv_previous.setOnClickListener(this);
        iv_playandpause.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_previous:
                listPosition = listPosition - 1;
                if (listPosition < 0) {
                    ShowToast.showShortToast(this, "没有上一首了！");
                    listPosition = listPosition + 1;
                } else {
                    Music music = mList.get(listPosition);
                    this.finish();
                    Intent intent = new Intent(this, PlayActivity.class);
                    intent.putExtra(MUSIC_POSITION, listPosition);
                    intent.putExtra(MUSIC_DATA, music);
                    startActivity(intent);
                    stopService(service);
                }
                break;
            case R.id.iv_playandpause:
                //播放和暂停
                if (isFirst) {
                    isFirst = false;
                    isPause = false;
                    iv_playandpause.setImageResource(R.drawable.ic_pause);
                    service.putExtra("url", music.getUrl());
                    service.putExtra("MSG", Config.PLAY_MSG);
                    startService(service);
                } else if (isPause) {
                    isPause = false;
                    iv_playandpause.setImageResource(R.drawable.ic_pause);
                    service.putExtra("MSG", Config.CONTINUE_MSG);
                    startService(service);
                } else {
                    isPause = true;
                    iv_playandpause.setImageResource(R.drawable.ic_play);
                    service.putExtra("MSG", Config.PAUSE_MSG);
                    startService(service);
                }
                break;
            case R.id.iv_next:
                listPosition = listPosition + 1;
                if (listPosition > mList.size() - 1) {
                    ShowToast.showShortToast(this, "没有下一首了！");
                    listPosition = listPosition - 1;
                } else {
                    Music music = mList.get(listPosition);
                    this.finish();
                    Intent intent = new Intent(this, PlayActivity.class);
                    intent.putExtra(MUSIC_POSITION, listPosition);
                    intent.putExtra(MUSIC_DATA, music);
                    startActivity(intent);
                    stopService(service);
                }
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.mSeekBar:
                if (fromUser) {
                    audioTrackChange(progress);
                }
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    //控制进度条改变播放进度
    public void audioTrackChange(int progress) {
        service.putExtra("MSG", Config.PROGRESS_CHANGE);
        service.putExtra("progress", progress);
        startService(service);
    }

    //定义和注册广播接收器
    private void registerReceiver() {
        playerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MUSIC_CURRENT);
        filter.addAction(MUSIC_FINISH);
        registerReceiver(playerReceiver, filter);
    }

    public class PlayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(MUSIC_CURRENT)) {
                    currentTime = intent.getIntExtra("currentTime", -1);
                    tv_currentTime.setText(MusicUtils.formatTime(currentTime));
                    mSeekBar.setProgress(currentTime);
                } else if (action.equals(MUSIC_FINISH)) {
                    boolean isFinish = intent.getBooleanExtra("isFinish", true);
                    if (isFinish) {
                        iv_playandpause.setImageResource(R.drawable.ic_play);
                        isFirst = true;
                        isPause = true;
                    }
                }
            }
        }
    }


}
