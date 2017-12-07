package com.sample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.sina.sinavideo.coreplayer.ISplayerVideoView;
import com.sina.sinavideo.sdk.VDDefine;
import com.sina.sinavideo.sdk.VDInterfaceFactory;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import master.flame.danmaku.danmaku.model.BaseDanmaku;

/**
 * Created by guoyao on 2017/12/7.
 */

public class VDDanmakuView extends FrameLayout {
    public static final int MaxCount = 6;

    public static final int Width = 200;

    public static final int Height = 150;

    private static HashMap<BaseDanmaku ,VDDanmakuBox> mUsingMap = new HashMap<>();

    private static LinkedList<VDDanmakuBox> mFreeList = new LinkedList<>();

    public VDDanmakuView( Context context) {
        super(context);
    }

    public VDDanmakuView(Context context, AttributeSet attrs){
        super(context,attrs);
        VDDanmakuBox box = createBox();
        if(box != null) {
            mFreeList.add(box);
        }
    }

    public boolean addDanmaku(BaseDanmaku danmaku, String playPath){
        if(danmaku == null){
            return false;
        }
        VDDanmakuBox box = null;
        if(!mFreeList.isEmpty()){
            box = mFreeList.removeFirst();
        }else if(mUsingMap.size() < MaxCount){
            box = createBox();
        }
        if(box != null) {
            box.use(danmaku, playPath);
            mUsingMap.put(danmaku, box);
            return true;
        }
        return false;
    }

    public void remove(BaseDanmaku danmaku){
        VDDanmakuBox box = mUsingMap.remove(danmaku);
        if(box != null){
            box.noUse();
            mFreeList.add(box);
        }
    }

    public void pause(){
        for (Map.Entry<BaseDanmaku, VDDanmakuBox> entry : mUsingMap.entrySet()) {
            entry.getValue().getVideoView().pause();
        }
    }

    public void resume(){
        for (Map.Entry<BaseDanmaku, VDDanmakuBox> entry : mUsingMap.entrySet()) {
            entry.getValue().getVideoView().start();
        }
    }



    public void release(boolean justFree){
        for(VDDanmakuBox b : mFreeList){
            b.release();
        }
        mFreeList.clear();
        if(!justFree) {
            for (Map.Entry<BaseDanmaku, VDDanmakuBox> entry : mUsingMap.entrySet()) {
                entry.getValue().release();
            }
            mUsingMap.clear();
        }
    }

    public void prepared(BaseDanmaku baseDanmaku){
        VDDanmakuBox box = mUsingMap.get(baseDanmaku);
        if(box != null){
            box.prepared();
        }
    }

    public ISplayerVideoView get(BaseDanmaku baseDanmaku){
        VDDanmakuBox box = mUsingMap.get(baseDanmaku);
        return box == null?null : box.getVideoView();
    }

    private VDDanmakuBox createBox(){
        ISplayerVideoView sinaVideoView = (ISplayerVideoView)VDInterfaceFactory.createVideoView(getContext(), VDDefine.VideoView.TYPE_TEXTURE_VIEW);
        if(sinaVideoView == null)
            return null;
        addView((View)sinaVideoView, Width, Height);
        ((View) sinaVideoView).setX(-Width);
        ((View) sinaVideoView).setVisibility(INVISIBLE);
        sinaVideoView.setMute(true);
        return new VDDanmakuBox(sinaVideoView);
    }
}
