package com.sample;

import com.sina.sinavideo.sdk.utils.VDApplication;

/**
 * Created by guoyao on 2017/12/6.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VDApplication.getInstance().setContext(getApplicationContext());
    }
}
