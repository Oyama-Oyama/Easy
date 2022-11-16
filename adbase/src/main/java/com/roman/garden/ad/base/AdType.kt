package com.roman.garden.ad.base

enum class AdType(val value: String) {

    INTERSTITIAL("interstitial"),
    REWARDED_VIDEO("rewarded"),
    BANNER("banner"),
    NATIVE_AD("nativeAd"),
    APP_OPEN_AD("appOpenAd"),
    INVALID("invalid");


    open fun isValid() : Boolean {
        when(this){
            INTERSTITIAL, REWARDED_VIDEO, BANNER, NATIVE_AD, APP_OPEN_AD -> return true
        }
        return false
    }

    companion object{

        open fun getAdType(str: String?) : AdType {
            when(str){
                INTERSTITIAL.value -> return INTERSTITIAL
                REWARDED_VIDEO.value -> return REWARDED_VIDEO
                BANNER.value -> return BANNER
                NATIVE_AD.value -> return NATIVE_AD
                APP_OPEN_AD.value -> return APP_OPEN_AD
            }
            return INVALID
        }

    }

}