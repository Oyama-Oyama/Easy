package com.roman.garden.common

import android.content.Context
import com.tencent.mmkv.MMKV

open class DiskUtil {

    companion object {

        fun init(context: Context) {
            MMKV.initialize(context)
        }

        private fun getInstance(): MMKV {
            return MMKV.defaultMMKV(MMKV.SINGLE_PROCESS_MODE, "cache")
        }

        fun putValue(key: String, value: Any) {
            if (value is Boolean) {
                getInstance().encode(key, value)
            } else if (value is String) {
                getInstance().encode(key, value.toString())
            } else if (value is Int) {
                getInstance().encode(key, value)
            } else if (value is Long) {
                getInstance().encode(key, value)
            } else if (value is Float) {
                getInstance().encode(key, value)
            } else if (value is Double) {
                getInstance().encode(key, value)
            }
        }

        fun getValue(key: String, def: Boolean): Boolean {
            return getInstance().decodeBool(key, def)
        }

        fun getValue(key: String, def: String?): String? {
            return getInstance().decodeString(key, def)
        }

        fun getValue(key: String, def: Int): Int {
            return getInstance().decodeInt(key, def)
        }

        fun getValue(key: String, def: Long): Long {
            return getInstance().decodeLong(key, def)
        }

        fun getValue(key: String, def: Float): Float {
            return getInstance().decodeFloat(key, def)
        }

        fun getValue(key: String, def: Double): Double {
            return getInstance().decodeDouble(key, def)
        }
    }

}