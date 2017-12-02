package com.example.guohouxiao.mp3demo.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;

import com.example.guohouxiao.mp3demo.utils.Config;

import java.io.IOException;

/**
 * Created by guohouxiao on 2017/11/25.
 * 播放器服务
 */

public class PlayerService extends Service {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String path;
    private int currentPosition;//当前播放进度

    public static final String MUSIC_CURRENT = "com.example.guohouxiao.MUSIC_CURRENT";//当前音乐播放时间更新动作
    public static final String MUSIC_FINISH = "com.example.guohouxiao.MUSIC_FINISH";//音乐播放完成时更新动作

    /**
     * handler用来接收消息，来发送广播更新播放时间
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (mediaPlayer != null) {
                    currentPosition = mediaPlayer.getCurrentPosition(); // 获取当前音乐播放的位置
                    Intent intent = new Intent();
                    intent.setAction(MUSIC_CURRENT);
                    intent.putExtra("currentTime", currentPosition);
                    sendBroadcast(intent); // 给PlayerActivity发送广播
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //播放完成时向activity发送广播
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Intent intent = new Intent();
                intent.setAction(MUSIC_FINISH);
                intent.putExtra("isFinish", true);
                sendBroadcast(intent);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int msg = intent.getIntExtra("MSG", 0);
        path = intent.getStringExtra("url");
        if (msg == Config.PLAY_MSG) {
            playMusic(0);
        } else if (msg == Config.PAUSE_MSG) {
            mediaPlayer.pause();
        } else if (msg == Config.CONTINUE_MSG) {
            mediaPlayer.start();
        } else if (msg == Config.PROGRESS_CHANGE) {
            currentPosition = intent.getIntExtra("progress", -1);
            playMusic(currentPosition);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override

    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    //播放音乐
    private void playMusic(int currentTime) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));
            handler.sendEmptyMessage(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class PreparedListener implements MediaPlayer.OnPreparedListener {

        private int currentTime;

        public PreparedListener(int currentTime) {
            this.currentTime = currentTime;
        }

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            if (currentTime > 0) {
                mediaPlayer.seekTo(currentTime);
            }
            mediaPlayer.start();
        }

    }

}
