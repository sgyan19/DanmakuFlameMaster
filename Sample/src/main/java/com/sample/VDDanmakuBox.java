package com.sample;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;

import com.sina.sinavideo.coreplayer.ISplayerVideoView;

import master.flame.danmaku.danmaku.model.BaseDanmaku;

/**
 * Created by guoyao on 2017/12/7.
 */

public class VDDanmakuBox {

    private BaseDanmaku baseDanmaku;

    private ISplayerVideoView mVideoView;

    private String mPlayPath;

    private SpaceDrawable mDrawable;

    public VDDanmakuBox(ISplayerVideoView view){
        mVideoView = view;
    }

    public void release(){
        baseDanmaku = null;
        mVideoView.release();
        mVideoView = null;
    }

    public void use(BaseDanmaku danmaku, String path){
        baseDanmaku = danmaku;
        String t = "s";
        mDrawable = new SpaceDrawable();
        if(((View) mVideoView).getMeasuredWidth() == 0 || ((View) mVideoView).getMeasuredHeight() == 0){
            mDrawable.setBounds(0,0,VDDanmakuView.Width, VDDanmakuView.Height);
        }else{
            mDrawable.setBounds(0, 0, ((View) mVideoView).getMeasuredWidth(), ((View) mVideoView).getMeasuredHeight());
        }

        CenterImageSpan span = new CenterImageSpan(mDrawable);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(t);
        spannableStringBuilder.setSpan(span, 0, t.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(danmaku.text);
        danmaku.text = spannableStringBuilder;
        mPlayPath = path;
    }

    public void noUse(){
        baseDanmaku = null;
        mVideoView.stopPlayback();
        ((View)mVideoView).setVisibility(View.INVISIBLE);
        mDrawable = null;
    }

    public ISplayerVideoView getVideoView(){
        return mVideoView;
    }

    private Runnable preparedRunnable = new Runnable() {
        @Override
        public void run() {
            if (mVideoView != null) {
                mVideoView.setVideoPath(mPlayPath);
                mVideoView.pause();
                ((View) mVideoView).setVisibility(View.VISIBLE);
            }
        }
    };

    public void prepared(){
        if(Looper.myLooper() == Looper.getMainLooper()) {
            preparedRunnable.run();
        }else{
            if (mVideoView != null){
                ((View) mVideoView).post(preparedRunnable);
            }
        }
    }
    private class SpaceDrawable extends Drawable{
    @Override
    public void draw(@NonNull Canvas canvas) {
        if(mVideoView != null && baseDanmaku != null){
            ((View)mVideoView).setX(baseDanmaku.getLeft() + 1);
            ((View)mVideoView).setY(baseDanmaku.getTop() + 1);
            if(!mVideoView.isPlaying()){
                mVideoView.start();
            }
        }
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    }
}
