package com.roman.garden.adbase

enum class AdPlatform(val value: String) {


    ADMOB("admob"),
    UNITY("unity"),
    VUNGLE("vungle"),
    INVALID("invalid");

    open fun isValid() : Boolean {
        return this.let {
            when(this){
                ADMOB, UNITY, VUNGLE -> true
                else -> false
            }
        }
    }

    companion object {

        fun getPlatform(str: String?) : AdPlatform {
            return str?.let {
                when(it){
                    ADMOB.value -> ADMOB
                    UNITY.value -> UNITY
                    VUNGLE.value -> VUNGLE
                    else -> INVALID
                }
            } ?: INVALID
        }

    }

}