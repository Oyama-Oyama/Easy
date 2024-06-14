package com.roman.gurdan.sudo.pro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import com.roman.garden.core.Easy

class NativeActivity : Base() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native)

        val n1 = findViewById<FrameLayout>(R.id.n1)
        val n2 = findViewById<FrameLayout>(R.id.n2)
        val n3 = findViewById<FrameLayout>(R.id.n3)
        val n4 = findViewById<FrameLayout>(R.id.n4)
        val n5 = findViewById<FrameLayout>(R.id.n5)

      //  var handler: Handler = Handler(Looper.getMainLooper())

        Easy.instance.showNative(n1)
        Easy.instance.showNative(n2)
        Easy.instance.showNative(n3)
        Easy.instance.showNative(n4)
        Easy.instance.showNative(n5)
//        handler.postDelayed({ Easy.instance.showNative(n2) }, 2000)
//        handler.postDelayed({ Easy.instance.showNative(n3) }, 4000)
//        handler.postDelayed({ Easy.instance.showNative(n4) }, 6000)
//        handler.postDelayed({ Easy.instance.showNative(n5) }, 8000)


    }


}