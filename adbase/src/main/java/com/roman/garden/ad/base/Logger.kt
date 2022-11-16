package com.roman.garden.ad.base

import android.util.Log

class Logger {

    companion object {

        private val TAG: String = "AdEasy"
        private var _enable: Boolean = true

        open fun enableLog(){
            _enable = true
        }

        open fun disableLog(){
            _enable = false
        }

        open fun v(message: String){
            Log.v(TAG, message)
        }

        open fun d(message: String){
            Log.d(TAG, message)
        }

        open fun i(message: String){
            Log.i(TAG, message)
        }

        open fun w(message: String){
            Log.w(TAG, message)
        }

        open fun e(message: String){
            Log.e(TAG, message)
        }

    }


}