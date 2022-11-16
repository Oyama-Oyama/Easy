package com.roman.garden.ad.base

class AdPlatformManager {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AdPlatformManager() }
    }

    var platforms: Array<Platform> = arrayOf()

    fun plusPlatform(platform: Platform){
        when(platform.isValid()){
            true -> platforms = platforms.plus(platform)
            else -> Logger.w("platform invalid:$platform")
        }
    }

}