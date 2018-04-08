package com.example.foodordering;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.util.Util;

public class Activity_VideoSurveillance extends BaseActivity {
    private VideoView videoViewVideo;
    private ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_surveillance);
        initView();
    }

    private void initView() {
        pg=new ProgressDialog(Activity_VideoSurveillance.this);
        pg.setMessage("缓冲中...");
        pg.show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("视频监控");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (!Util.checkNetwork(this)) {
            return;
        }
        videoViewVideo = (VideoView) findViewById(R.id.videoViewVideo);

        try {
            //设置视频控制器
            MediaController mediacontroller = new MediaController(Activity_VideoSurveillance.this);
            mediacontroller.setAnchorView(videoViewVideo);
            videoViewVideo.setMediaController(mediacontroller);
            //视频来源
            videoViewVideo.setVideoURI(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));
        } catch (Exception e) {
            e.printStackTrace();
            pg.dismiss();
        }
        videoViewVideo.requestFocus();
        videoViewVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pg.dismiss();
                //开始播放视频
                videoViewVideo.start();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
