package com.roman.garden.ad.core.ad.manager

import com.roman.garden.adbase.AdType
import com.roman.garden.adbase.AdUnit
import com.roman.garden.adbase.Platform

internal class AdConfigManager private constructor() {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AdConfigManager() }
    }

    private var bannerConfig: BannerAdConfig? = null
    private var rewardedConfig: RewardedAdConfig? = null
    private var interstitialConfig: InterstitialAdConfig? = null
    private var appOpenConfig: AppOpenAdConfig? = null
    private var nativeConfig: NativeAdConfig? = null
    private val platforms = mutableListOf<Platform>()
    private val maxCacheAdMap = mutableMapOf<String, Int>().apply {
        this[AdType.INTERSTITIAL.value] = 2
        this[AdType.REWARDED_VIDEO.value] = 2
        this[AdType.NATIVE_AD.value] = 2
        this[AdType.APP_OPEN_AD.value] = 1
        this[AdType.BANNER.value] = -1
    }

    var testMode = false

    fun isTestMode():Boolean = testMode

    fun addAdUnit(adUnit: AdUnit) = findConfig(adUnit.adType)?.let { it.addAdUnit(adUnit) }

    fun getAdUnits(adType: AdType) = findConfig(adType)?.adUnits

    fun findNextAdUnits(adType: AdType, adUnit: AdUnit?): List<AdUnit> =
        findConfig(adType)?.let { it.findNextAdUnits(adUnit) } ?: emptyList<AdUnit>()

    fun addPlatform(platform: Platform) = platforms.add(platform)

    fun getPlatforms(): MutableList<Platform> = platforms

    fun setBannerRefreshDuration(duration: Int) =
        findConfig(AdType.BANNER)?.let { it.refreshDuration = duration }

    fun getBannerRefreshDuration(): Int =
        findConfig(AdType.BANNER)?.let { it.refreshDuration } ?: 20

    fun setNativeAdRefreshDuration(duration: Int) =
        findConfig(AdType.NATIVE_AD)?.let { it.refreshDuration = duration }

    fun getNativeAdRefreshDuration(): Int =
        findConfig(AdType.NATIVE_AD)?.let { it.refreshDuration } ?: 20

    fun updateMaxCacheAdMap(type: String, max: Int) = max.also { maxCacheAdMap[type] = it }

    fun getMaxCachedCount(type: String): Int = maxCacheAdMap[type]?.also { } ?: -1

    fun getAdConfig(adType: AdType): IAdConfig? {
        return when (adType) {
            AdType.APP_OPEN_AD -> appOpenConfig
            AdType.REWARDED_VIDEO -> rewardedConfig
            AdType.INTERSTITIAL -> interstitialConfig
            AdType.BANNER -> bannerConfig
            AdType.NATIVE_AD -> nativeConfig
            else -> null
        }
    }

    fun sortAdUnits() {
        bannerConfig?.sortAdUnits()
        rewardedConfig?.sortAdUnits()
        interstitialConfig?.sortAdUnits()
        appOpenConfig?.sortAdUnits()
        nativeConfig?.sortAdUnits()
    }

    private fun findConfig(adType: AdType): IAdConfig? {
        return when (adType) {
            AdType.APP_OPEN_AD -> {
                appOpenConfig ?: appOpenConfig.also { appOpenConfig = AppOpenAdConfig() }
            }
            AdType.BANNER -> {
                bannerConfig ?: bannerConfig.also { bannerConfig = BannerAdConfig() }
            }
            AdType.REWARDED_VIDEO -> {
                rewardedConfig ?: rewardedConfig.also { rewardedConfig = RewardedAdConfig() }
            }
            AdType.INTERSTITIAL -> {
                interstitialConfig ?: interstitialConfig.also {
                    interstitialConfig = InterstitialAdConfig()
                }
            }
            AdType.NATIVE_AD -> {
                nativeConfig ?: nativeConfig.also { nativeConfig = NativeAdConfig() }
            }
            else -> null
        }
    }


}