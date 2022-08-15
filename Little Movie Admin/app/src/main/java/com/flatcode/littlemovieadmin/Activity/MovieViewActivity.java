package com.flatcode.littlemovieadmin.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemovieadmin.R;
import com.flatcode.littlemovieadmin.Unit.CLASS;
import com.flatcode.littlemovieadmin.Unit.DATA;
import com.flatcode.littlemovieadmin.Unit.THEME;
import com.flatcode.littlemovieadmin.Unit.VOID;
import com.flatcode.littlemovieadmin.databinding.ActivityMovieViewBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class MovieViewActivity extends AppCompatActivity {

    private ActivityMovieViewBinding binding;
    Activity activity = MovieViewActivity.this;

    Uri videoUri;
    ExoPlayer exoPlayer;
    ExtractorsFactory extractorsFactory;
    ImageView exo_floating_widget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullScreen();
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityMovieViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        exo_floating_widget = findViewById(R.id.exo_floating_widget);

        Intent intent = getIntent();
        if (intent != null) {
            String uriValue = intent.getStringExtra(DATA.MOVIE_LINK);
            videoUri = Uri.parse(uriValue);
        }

        exo_floating_widget.setOnClickListener(v -> {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.release();
            Intent service = new Intent(activity, CLASS.SERVICE);
            service.putExtra(DATA.MOVIE_LINK, videoUri.toString());
            startService(service);
        });

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector
                (new AdaptiveTrackSelection.Factory(bandwidthMeter));
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        extractorsFactory = new DefaultExtractorsFactory();
        playVideo();
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void playVideo() {
        try {
            String playerInfo = Util.getUserAgent(this, "MovieAppClient");
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, playerInfo);
            MediaSource mediaSource = new ExtractorMediaSource(
                    videoUri, dataSourceFactory, extractorsFactory, null, null);
            binding.playerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.release();
    }
}