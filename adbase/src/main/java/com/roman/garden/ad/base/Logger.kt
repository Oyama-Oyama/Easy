package com.roman.garden.ad.base

import android.util.Log

class Logger {

    companion object {

        private val TAG: String = "AdEasy"
        private var _enable: Boolean = true

        fun enableLog(){
            _enable = true
        }

        fun disableLog(){
            _enable = false
        }

        fun v(message: String){
            Log.v(TAG, message)
        }

        fun d(message: String){
            Log.d(TAG, message)
        }

        fun i(message: String){
            Log.i(TAG, message)
        }

        fun w(message: String){
            Log.w(TAG, message)
        }

        fun e(message: String){
            Log.e(TAG, message)
        }

    }


}