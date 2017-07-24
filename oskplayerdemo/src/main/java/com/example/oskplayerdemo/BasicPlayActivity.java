package com.example.oskplayerdemo;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.tencent.oskplayer.QPlayer;
import com.tencent.oskplayer.player.IBaseMediaPlayer;
import com.tencent.oskplayer.player.StateMediaPlayer;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leoliu on 17/3/2.
 */

public class BasicPlayActivity extends Activity {
    public static final String LOG_TAG = "BasicPlayActivity";
    private InternalListener mListener;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    SeekBar seekBar;
    QPlayer qPlayer;
    final int SIMPALE_PALYER = 1;
    final int EXTEND_PALYER = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_play_activity);
        initUI();
        initQPlayer();
    }
    private void initUI() {
        Button startPlay = (Button) findViewById(R.id.start_play);
        Button pausePlay = (Button)findViewById(R.id.pause_play);
        Button stopPlay = (Button)findViewById(R.id.stop_play);
        seekBar = (SeekBar)findViewById(R.id.seekBar01);
        surfaceView = (SurfaceView)findViewById(R.id.surfaceView01);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        mListener = new InternalListener();
        startPlay.setOnClickListener(mListener);
        pausePlay.setOnClickListener(mListener);
        stopPlay.setOnClickListener(mListener);
        seekBar.setOnSeekBarChangeListener(new SeekBarImpListener());
    }

    protected void initQPlayer(){
        qPlayer = new QPlayer();
        /** 启用调式 debug */
        qPlayer.setDebugVersion(false);

        /** 设置 headers 部分*/
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("x-cos-player","QPlayer");
        requestHeaders.put("Refers","http://www.bilibili.com/video/av/1491980066");
        requestHeaders.put("User-Agent", "python-spider");
        Log.w(LOG_TAG, "set x-cos-player & Refers");
        qPlayer.setRequestHeaders(requestHeaders);
    }

    private class InternalListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.start_play) {
                if(qPlayer.isPaused()){
                    try {
                        qPlayer.start();
                    } catch (IBaseMediaPlayer.InternalOperationException e) {
                        e.printStackTrace();
                    }
                    StringBuffer sb = new StringBuffer();
                    sb.append("current = ").append(qPlayer.getCurrentPosition()).append("\n")
                            .append("width = ").append(qPlayer.getVideoWidth()).append("\n")
                            .append("height = ").append(qPlayer.getVideoHeight()).append("\n")
                            .append("duration = ").append(qPlayer.getDuration()).append("\n")
                            .append("islooping = ").append(qPlayer.isLooping()).append("\n")
                            .append("ispause = ").append(qPlayer.isPaused()).append("\n")
                            .append("isplaying = ").append(qPlayer.isPlaying()).append("\n")
                            .append("sessionid = ").append(qPlayer.getAudioSessionId()).append("\n");
                    Log.w("PLAYING", sb.toString());
                }else if (!qPlayer.isPlaying()){
                    startPlay();
                }
            }
            if (view.getId() == R.id.pause_play) {
                if(qPlayer != null){
                    try {
                        qPlayer.pause();
                        StringBuffer sb = new StringBuffer();
                        sb.append("current = ").append(qPlayer.getCurrentPosition()).append("\n")
                                .append("width = ").append(qPlayer.getVideoWidth()).append("\n")
                                .append("height = ").append(qPlayer.getVideoHeight()).append("\n")
                                .append("duration = ").append(qPlayer.getDuration()).append("\n")
                                .append("islooping = ").append(qPlayer.isLooping()).append("\n")
                                .append("ispause = ").append(qPlayer.isPaused()).append("\n")
                                .append("isplaying = ").append(qPlayer.isPlaying()).append("\n")
                                .append("sessionid = ").append(qPlayer.getAudioSessionId()).append("\n");
                        Log.w("PAUSE", sb.toString());
                    } catch (IBaseMediaPlayer.InternalOperationException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (view.getId() == R.id.stop_play) {
                if(qPlayer != null){
                    qPlayer.stop();
                    qPlayer.release();
                    Log.w("WU","ispause" + new Boolean(qPlayer.isPaused()));
                }
            }
        }
    }

    private void startPlay() {
        /** 本地视频*/
        String url1 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"1.mp4";

        /** 网络视频*/
        String url2 = "http://fransvideo-1251668577.cossh.myqcloud.com/%E5%95%A6%E5%95%A6%E5%95%A6.mov";
        String url3 = "http://xy2-1251668577.cosgz.myqcloud.com/big.mp4";

        List<String> list = new ArrayList<>();
        list.add(url2);
        list.add(url3);
       // list.add(url1);

        //单个url播放
//        setDataSource(SIMPALE_PALYER,list,0);

        //多个url播放
        long totalDuration = 21772L + 1231726L;
        setDataSource(EXTEND_PALYER,list,totalDuration);

        setListener();

        qPlayer.prepareAsync();
    }

    private class SeekBarImpListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(qPlayer != null && fromUser){
                long total = qPlayer.getDuration();
                int seekTo = (int) (progress * (total/100.0));
                Log.w(LOG_TAG,"seekTo =" + seekTo + "/" + total);
                qPlayer.seekTo(seekTo);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        qPlayer.stop();
        qPlayer.release();
    }


    protected void setDataSource(int mode, List<String> urls, long totalDuration){

        /** 简单播放 */
        if(mode == SIMPALE_PALYER){
            qPlayer.initPlayer(getApplicationContext(), QPlayer.SIMPLE_PLAYER);
            try {
                qPlayer.setDataSource(urls.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /** URLs数组播放*/
        if(mode == EXTEND_PALYER){
            qPlayer.initPlayer(getApplicationContext(), QPlayer.EXTEND_PLAYER);
            qPlayer.setDataSource(urls,totalDuration);
        }
        Log.w(LOG_TAG, new Boolean(qPlayer.isLooping()).toString());
        /** 播放器属性：volume、surface、audioStream等*/
        setPlayerProperty();
    }

    protected void setPlayerProperty(){
        qPlayer.setScreenOnWhilePlaying(true);
        qPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
        qPlayer.setVolume(0.1f, 0.1f);
        qPlayer.setMode(StateMediaPlayer.MODE_VIDEO);
        qPlayer.setLooping(false);
        qPlayer.setDisplay(surfaceHolder);
    }

    protected void setListener(){
        /** 准备ok -> start */
        qPlayer.setOnPreparedListener(new IBaseMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IBaseMediaPlayer iBaseMediaPlayer) {
                try {

                    qPlayer.start();
                    StringBuilder stringBuilder = new StringBuilder("Prepared:\n");
                    stringBuilder.append("duration = ").append(qPlayer.getDuration()).append("\n")
                            .append("videoHeigth = ").append(qPlayer.getVideoHeight()).append("\n")
                            .append("videoWidth = ").append(qPlayer.getVideoWidth()).append("\n")
                            .append("current = ").append(qPlayer.getCurrentPosition()).append("\n");
                    Log.w(LOG_TAG,stringBuilder.toString());

                } catch (IBaseMediaPlayer.InternalOperationException e) {
                    e.printStackTrace();
                }
            }
        });

        /** 播放结束 */
        qPlayer.setOnCompletionListener(new IBaseMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IBaseMediaPlayer iBaseMediaPlayer) {
                Log.w(LOG_TAG,"completed!");

            }
        });

        /** 缓冲结束 */
        qPlayer.setOnBufferingUpdateListener(new IBaseMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IBaseMediaPlayer iBaseMediaPlayer, int i) {
                StringBuilder stringBuilder = new StringBuilder("BufferUpdate\n");
                stringBuilder.append("update = " + i);
                Log.w(LOG_TAG,stringBuilder.toString());
            }
        });

        /** seekTo结束 */
        qPlayer.setOnSeekCompleteListener(new IBaseMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(IBaseMediaPlayer iBaseMediaPlayer) {
                Log.w(LOG_TAG,"SeekComplete!");

            }
        });

        /** video size 改变*/
        qPlayer.setOnVideoSizeChangedListener(new IBaseMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IBaseMediaPlayer iBaseMediaPlayer, int i, int i1) {
                StringBuilder stringBuilder = new StringBuilder("SizeChanged\n");
                stringBuilder.append("sizeChanaged = " + i + "; " + i1);

                Log.w(LOG_TAG,stringBuilder.toString());
            }
        });

        /** video info */
        qPlayer.setOnInfoListener(new IBaseMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IBaseMediaPlayer iBaseMediaPlayer, int i, int i1) {
                StringBuilder stringBuilder = new StringBuilder("Info\n");
                stringBuilder.append("Info = " + i + "; " + i1);
                Log.w(LOG_TAG,stringBuilder.toString());
                return false;
            }
        });

        /** 播放出错 */
        qPlayer.setOnErrorListener(new IBaseMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IBaseMediaPlayer iBaseMediaPlayer, int i, int i1) {
                StringBuilder stringBuilder = new StringBuilder("Error\n");
                stringBuilder.append("Error = " + i + "; " + i1);
                Log.w(LOG_TAG,stringBuilder.toString());
                return false;
            }
        });
    }
}
