package com.roman.garden.ad.base

import kotlin.math.abs

class AdImpl {

    lateinit var adUnit: AdUnit
    var loadStartTime: Long = 0
    var cacheStartTime: Long = 0
    var ad: Any? = null

    companion object {
        const val MAX_LOAD_DURATION = 60 * 1000L
        const val MAX_CACHE_DURATION = 60 * 60 * 1000L
    }

    fun isValidLoad() : Boolean {
        if (abs(System.currentTimeMillis() - loadStartTime) > MAX_LOAD_DURATION)
            return false
        return true
    }

    fun isValidCache() : Boolean {
        if (abs(System.currentTimeMillis() - cacheStartTime) > MAX_CACHE_DURATION)
            return false
        return true
    }

    fun resetLoadTime(){
        loadStartTime = System.currentTimeMillis()
    }

    fun resetCacheTime(){
        cacheStartTime = System.currentTimeMillis()
    }

    fun isValid() : Boolean {
        return this.run {
            return adUnit != null && adUnit!!.isValid()
        }
    }

    override fun toString(): String {
        return "AdImpl(adUnit=$adUnit, loadStartTime=$loadStartTime, cacheStartTime=$cacheStartTime, ad=$ad)"
    }


}