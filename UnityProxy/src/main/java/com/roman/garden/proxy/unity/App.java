package com.roman.garden.proxy.unity;

import android.app.Application;

import com.roman.garden.base.log.Logger;
import com.roman.garden.core.Easy;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.Companion.w("App OnCreate");
        Easy.Companion.getInstance().init(this, false);
    }
}
