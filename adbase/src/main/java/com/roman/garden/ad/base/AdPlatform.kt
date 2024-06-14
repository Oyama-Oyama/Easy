package com.roman.garden.ad.base

enum class AdPlatform(val value: String) {

    ADMOB("admob"),
    UNITY("unity"),
    INVALID("invalid");

    open fun isValid(): Boolean {
        return when (this) {
            ADMOB, UNITY -> true
            else -> false
        }
    }

    companion object {

        open fun getPlatform(str: String?): AdPlatform {
            when (str) {
                ADMOB.value -> return ADMOB
                UNITY.value -> return UNITY
            }
            return INVALID
        }

    }

}