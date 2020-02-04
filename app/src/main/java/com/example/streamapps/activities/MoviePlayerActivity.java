package com.example.streamapps.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.example.streamapps.R;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import android.net.Uri;
import android.os.Bundle;

import android.view.WindowManager;

public class MoviePlayerActivity extends AppCompatActivity {
    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;
    String videoURL;
    public static final String VIDEO_TEST_URL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_player);

        setFullScreen();
        hideActionBar();
        iniExoPlayer();
    }

    private void hideActionBar() {
        getSupportActionBar().hide();
    }

    private void setFullScreen() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void iniExoPlayer() {

        playerView = findViewById(R.id.movie_exo_player);
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(simpleExoPlayer);
        videoURL = getIntent().getExtras().getString("videoURL");

        DataSource.Factory dsFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "appname"));
        MediaSource videoSource;
        videoSource = new ExtractorMediaSource.Factory(dsFactory).createMediaSource(Uri.parse(videoURL));
        simpleExoPlayer.prepare(videoSource);
        simpleExoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleExoPlayer.release();
    }
}
