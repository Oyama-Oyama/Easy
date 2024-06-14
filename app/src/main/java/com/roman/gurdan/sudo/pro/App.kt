package com.roman.gurdan.sudo.pro

import android.app.Application
import com.roman.garden.core.Easy


class App : Application() {


    override fun onCreate() {
        super.onCreate()
       Easy.instance.init(this, true)
    }

}