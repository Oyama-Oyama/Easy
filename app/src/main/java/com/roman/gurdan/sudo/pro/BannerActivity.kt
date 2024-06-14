package com.roman.gurdan.sudo.pro

import android.os.Bundle
import com.roman.garden.core.Easy

class BannerActivity : Base() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_native)
    }

    override fun onStart() {
        super.onStart()
        Easy.instance.showBanner()
    }

    override fun onStop() {
        super.onStop()
     //   Easy.instance.closeBanner()
    }

}