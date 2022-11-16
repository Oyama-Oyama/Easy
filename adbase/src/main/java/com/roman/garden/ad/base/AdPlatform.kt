package com.roman.garden.ad.base

enum class AdPlatform(val value: String) {

    ADMOB("admob"),
    UNITY("unity"),
    INVALID("invalid");

    open fun isValid() : Boolean {
        when(this){
            ADMOB, UNITY -> return true
        }
        return false
    }

    companion object {

        open fun getPlatform(str: String?) : AdPlatform {
            when(str){
                ADMOB.value -> return ADMOB
                UNITY.value -> return UNITY
            }
            return INVALID
        }

    }

}