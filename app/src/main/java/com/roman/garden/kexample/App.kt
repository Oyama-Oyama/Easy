package com.roman.garden.kexample

import android.app.Application
import com.roman.garden.core.Easy

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Easy.init(this)
    }


}