package com.roman.garden.core.ad

import android.app.Activity
import android.view.ViewGroup

internal interface AdEasyProxy {

    fun hasInterstitial() : Boolean

    fun hasRewardedVideo() : Boolean

    fun hasBanner() : Boolean

    fun hasAppOpenAd() : Boolean

    fun hasNativeAd() : Boolean

    fun showInterstitial(activity: Activity)

    fun showRewardedVideo(activity: Activity)

    fun showBanner(container: ViewGroup)

    fun hideBanner()

    fun showAppOpenAd(activity: Activity)

    fun showNativeAd(container: ViewGroup)

    fun hideNativeAd()

    fun setGlobalAdListener(adListener: AdListener)

    fun setGlobalAdEventListener(eventListener: EventListener)

    fun setInterstitialListener(adListener: AdListener)

    fun setRewardedVideoListener(adListener: AdListener)

    fun setAppOpenAdListener(adListener: AdListener)

    fun setBannerListener(adListener: AdListener)

    fun setNativeListener(adListener: AdListener)


}