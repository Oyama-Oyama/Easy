package com.roman.garden.core.ad

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import com.roman.garden.ad.base.AdStatus
import com.roman.garden.ad.base.AdType
import com.roman.garden.ad.base.AdUnit
import com.roman.garden.ad.base.AdUnitManager
import com.roman.garden.core.Easy
import com.roman.garden.core.ad.platform.PlatformManager
import com.roman.garden.core.ad.proxy.*

internal class AdEasy : AdEasyProxy {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AdEasy() }
    }

    fun getContext(): Context {
        return Easy.context
    }

    fun startAdTasks(context: Context) {
        PlatformManager.instance.initAdapters(context)
        AdUnitManager.instance.getConfigAdTypes().forEach { adType ->
            when (adType) {
                AdType.APP_OPEN_AD -> AppOpenProxy.instance.load(context, null)
                AdType.INTERSTITIAL -> InterstitialProxy.instance.load(context, null)
                AdType.REWARDED_VIDEO -> RewardedProxy.instance.load(context, null)
                AdType.BANNER -> BannerProxy.instance.load(context, null)
                AdType.NATIVE_AD -> NativeAdProxy.instance.load(context, null)
                else -> {}
            }
        }
    }

    fun notifyAdShowFail(adType: AdType, reason: AdStatus) {
        when (adType) {
            AdType.INTERSTITIAL -> InterstitialProxy.instance.onAdShowFail(AdUnit(), reason)
            AdType.REWARDED_VIDEO -> RewardedProxy.instance.onAdShowFail(AdUnit(), reason)
            AdType.APP_OPEN_AD -> AppOpenProxy.instance.onAdShowFail(AdUnit(), reason)
            AdType.BANNER -> BannerProxy.instance.onAdShowFail(AdUnit(), reason)
            AdType.NATIVE_AD -> NativeAdProxy.instance.onAdShowFail(AdUnit(), reason)
            else -> {}
        }
    }

    override fun hasInterstitial(): Boolean {
        return InterstitialProxy.instance.hasValidAd()
    }

    override fun hasRewardedVideo(): Boolean {
        return RewardedProxy.instance.hasValidAd()
    }

    override fun hasBanner(): Boolean {
        return BannerProxy.instance.hasValidAd()
    }

    override fun hasAppOpenAd(): Boolean {
        return AppOpenProxy.instance.hasValidAd()
    }

    override fun hasNativeAd(): Boolean {
        return NativeAdProxy.instance.hasValidAd()
    }

    override fun showInterstitial(activity: Activity) {
        InterstitialProxy.instance.show(activity)
    }

    override fun showRewardedVideo(activity: Activity) {
        RewardedProxy.instance.show(activity)
    }

    override fun showBanner(container: ViewGroup) {
        BannerProxy.instance.show(container)
    }

    override fun hideBanner() {
        BannerProxy.instance.hide()
    }

    override fun showAppOpenAd(activity: Activity) {
        AppOpenProxy.instance.show(activity)
    }

    override fun showNativeAd(container: ViewGroup) {
        NativeAdProxy.instance.show(container)
    }

    override fun hideNativeAd() {
        NativeAdProxy.instance.hide()
    }

    override fun setGlobalAdListener(adListener: AdListener) {
        InterstitialProxy.instance.adListener = adListener
        RewardedProxy.instance.adListener = adListener
        BannerProxy.instance.adListener = adListener
        AppOpenProxy.instance.adListener = adListener
        NativeAdProxy.instance.adListener = adListener
    }

    override fun setGlobalAdEventListener(eventListener: EventListener) {
        InterstitialProxy.instance.eventListener = eventListener
        RewardedProxy.instance.eventListener = eventListener
        BannerProxy.instance.eventListener = eventListener
        AppOpenProxy.instance.eventListener = eventListener
        NativeAdProxy.instance.eventListener = eventListener
    }

    override fun setInterstitialListener(adListener: AdListener) {
        InterstitialProxy.instance.setPartListener(adListener)
    }

    override fun setRewardedVideoListener(adListener: AdListener) {
        RewardedProxy.instance.setPartListener(adListener)
    }

    override fun setAppOpenAdListener(adListener: AdListener) {
        AppOpenProxy.instance.setPartListener(adListener)
    }

    override fun setBannerListener(adListener: AdListener) {
        BannerProxy.instance.setPartListener(adListener)
    }

    override fun setNativeListener(adListener: AdListener) {
        NativeAdProxy.instance.setPartListener(adListener)
    }


}