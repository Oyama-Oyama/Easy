package com.roman.gurdan.sudo.pro

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

abstract class Base : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("ACTIVITY", "onCreate:" + this.localClassName.toString())
    }

    override fun onStart() {
        super.onStart()
        Log.e("ACTIVITY", "onStart:" + this.localClassName.toString())
    }

    override fun onResume() {
        super.onResume()
        Log.e("ACTIVITY", "onResume:" + this.localClassName.toString())
    }

    override fun onPause() {
        super.onPause()
        Log.e("ACTIVITY", "onPause:" + this.localClassName.toString())
    }

    override fun onStop() {
        super.onStop()
        Log.e("ACTIVITY", "onStop:" + this.localClassName.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("ACTIVITY", "onDestroy:" + this.localClassName.toString())
    }


}