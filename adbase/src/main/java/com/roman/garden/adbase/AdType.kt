package com.roman.garden.adbase

enum class AdType(val value: String) {

    INTERSTITIAL("interstitial"),
    REWARDED_VIDEO("rewarded"),
    BANNER("banner"),
    NATIVE_AD("nativeAd"),
    APP_OPEN_AD("appOpenAd"),
    INVALID("invalid");


    open fun isValid(): Boolean {
        return this.let {
            when (this) {
                INTERSTITIAL, REWARDED_VIDEO, BANNER, NATIVE_AD, APP_OPEN_AD -> true
                else -> false
            }
        }
    }

    companion object {

        fun getAdType(str: String?): AdType {
            return str?.let {
                when (it) {
                    INTERSTITIAL.value -> INTERSTITIAL
                    REWARDED_VIDEO.value -> REWARDED_VIDEO
                    BANNER.value -> BANNER
                    NATIVE_AD.value -> NATIVE_AD
                    APP_OPEN_AD.value -> APP_OPEN_AD
                    else -> INVALID
                }
            } ?: INVALID
        }

    }
}