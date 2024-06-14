package com.roman.garden.base.log

import android.util.Log

class Logger {

    companion object {

        private const val TAG: String = "AdEasy"
        private var enable: Boolean = true

        fun enableLog() {
            enable = true
        }

        fun disableLog() {
            enable = false
        }

        fun v(message: String) {
            Log.v(TAG, message)
        }

        fun d(message: String) {
            Log.d(TAG, message)
        }

        fun i(message: String) {
            Log.i(TAG, message)
        }

        fun w(message: String) {
            Log.w(TAG, message)
        }

        fun e(message: String) {
            Log.e(TAG, message)
        }
    }

}