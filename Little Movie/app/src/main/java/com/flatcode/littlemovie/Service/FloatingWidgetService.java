package com.flatcode.littlemovie.Service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.flatcode.littlemovie.Activity.MovieViewActivity;
import com.flatcode.littlemovie.R;
import com.flatcode.littlemovie.Unit.DATA;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class FloatingWidgetService extends Service {

    public FloatingWidgetService() {

    }

    WindowManager windowManager;
    private View FloatingWidget;
    Uri videoUri;
    SimpleExoPlayer exoPlayer;
    PlayerView playerView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String uriStr = intent.getStringExtra(DATA.MOVIE_LINK);
            String id = intent.getStringExtra(DATA.MOVIE_ID);
            videoUri = Uri.parse(uriStr);
            if (windowManager != null && FloatingWidget.isShown() && exoPlayer != null) {
                windowManager.removeView(FloatingWidget);
                FloatingWidget = null;
                windowManager = null;
                exoPlayer.setPlayWhenReady(false);
                exoPlayer.release();
                exoPlayer = null;
            }
            final WindowManager.LayoutParams params;
            FloatingWidget = LayoutInflater.from(this).inflate(R.layout.item_pop_up_window, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params = new WindowManager.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            } else {
                params = new WindowManager.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }
            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.x = 200;
            params.y = 200;

            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.addView(FloatingWidget, params);
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            playerView = FloatingWidget.findViewById(R.id.playerView);

            ImageView close = FloatingWidget.findViewById(R.id.close);
            ImageView maximize = FloatingWidget.findViewById(R.id.maximize);

            maximize.setOnClickListener(view -> {
                if (windowManager != null && FloatingWidget.isShown() && exoPlayer != null) {
                    windowManager.removeView(FloatingWidget);
                    FloatingWidget = null;
                    windowManager = null;
                    exoPlayer.setPlayWhenReady(false);
                    exoPlayer.release();
                    exoPlayer = null;
                    stopSelf();
                    Intent intent1 = new Intent(FloatingWidgetService.this,
                            MovieViewActivity.class);
                    intent1.putExtra(DATA.MOVIE_LINK, videoUri.toString());
                    intent1.putExtra(DATA.MOVIE_ID, id);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }
            });

            close.setOnClickListener(view -> {
                if (windowManager != null && FloatingWidget.isShown() && exoPlayer != null) {
                    windowManager.removeView(FloatingWidget);
                    FloatingWidget = null;
                    windowManager = null;
                    exoPlayer.setPlayWhenReady(false);
                    exoPlayer.release();
                    exoPlayer = null;
                    stopSelf();
                }
            });

            playVideos();

            FloatingWidget.findViewById(R.id.item).setOnTouchListener(new View.OnTouchListener() {

                private int initialX, initialY;
                private float initialTouchX, initialTouchY;

                @Override
                public boolean onTouch(View view, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = params.x;
                            initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;

                        case MotionEvent.ACTION_UP:
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(FloatingWidget, params);
                            return true;
                    }
                    return false;
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void playVideos() {
        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector
                    (new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(FloatingWidgetService.this, trackSelector);

            String playerInfo = Util.getUserAgent(this, "VideoPlayer");
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, playerInfo);

            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            MediaSource mediaSource = new ExtractorMediaSource(videoUri,
                    dataSourceFactory, extractorsFactory, null, null);
            playerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (FloatingWidget != null)
            windowManager.removeView(FloatingWidget);
    }
}