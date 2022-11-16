package com.roman.garden.core.ad.proxy

import com.roman.garden.ad.base.*

internal class AdapterListenerProxy : IAdLoadListener, IAdListener {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AdapterListenerProxy() }
    }

    var interstitialListener: AdapterListener? = null
    var rewardedListener: AdapterListener? = null
    var appOpenAdListener: AdapterListener? = null
    var bannerListener: AdapterListener? = null
    var nativeAdListener: AdapterListener? = null

    fun setListener(adType: AdType, listener: AdapterListener?) {
        when (adType) {
            AdType.INTERSTITIAL -> interstitialListener = listener
            AdType.REWARDED_VIDEO -> rewardedListener = listener
            AdType.APP_OPEN_AD -> appOpenAdListener = listener
            AdType.BANNER -> bannerListener = listener
            AdType.NATIVE_AD -> nativeAdListener = listener
        }
    }

    override fun onAdShow(adUnit: AdUnit) {
        when (adUnit.adType) {
            AdType.INTERSTITIAL -> interstitialListener?.onAdShow(adUnit)
            AdType.REWARDED_VIDEO -> rewardedListener?.onAdShow(adUnit)
            AdType.APP_OPEN_AD -> appOpenAdListener?.onAdShow(adUnit)
            AdType.BANNER -> bannerListener?.onAdShow(adUnit)
            AdType.NATIVE_AD -> nativeAdListener?.onAdShow(adUnit)
        }
    }

    override fun onAdShowFail(adUnit: AdUnit, reason: AdStatus) {
        when (adUnit.adType) {
            AdType.INTERSTITIAL -> interstitialListener?.onAdShowFail(adUnit, reason)
            AdType.REWARDED_VIDEO -> rewardedListener?.onAdShowFail(adUnit, reason)
            AdType.APP_OPEN_AD -> appOpenAdListener?.onAdShowFail(adUnit, reason)
            AdType.BANNER -> bannerListener?.onAdShowFail(adUnit, reason)
            AdType.NATIVE_AD -> nativeAdListener?.onAdShowFail(adUnit, reason)
        }
    }

    override fun onAdClicked(adUnit: AdUnit) {
        when (adUnit.adType) {
            AdType.INTERSTITIAL -> interstitialListener?.onAdClicked(adUnit)
            AdType.REWARDED_VIDEO -> rewardedListener?.onAdClicked(adUnit)
            AdType.APP_OPEN_AD -> appOpenAdListener?.onAdClicked(adUnit)
            AdType.BANNER -> bannerListener?.onAdClicked(adUnit)
            AdType.NATIVE_AD -> nativeAdListener?.onAdClicked(adUnit)
        }
    }

    override fun onAdClosed(adUnit: AdUnit) {
        when (adUnit.adType) {
            AdType.INTERSTITIAL -> interstitialListener?.onAdClosed(adUnit)
            AdType.REWARDED_VIDEO -> rewardedListener?.onAdClosed(adUnit)
            AdType.APP_OPEN_AD -> appOpenAdListener?.onAdClosed(adUnit)
            AdType.BANNER -> bannerListener?.onAdClosed(adUnit)
            AdType.NATIVE_AD -> nativeAdListener?.onAdClosed(adUnit)
        }
    }

    override fun onUserRewarded(adUnit: AdUnit) {
        when (adUnit.adType) {
            AdType.INTERSTITIAL -> interstitialListener?.onUserRewarded(adUnit)
            AdType.REWARDED_VIDEO -> rewardedListener?.onUserRewarded(adUnit)
            AdType.APP_OPEN_AD -> appOpenAdListener?.onUserRewarded(adUnit)
            AdType.BANNER -> bannerListener?.onUserRewarded(adUnit)
            AdType.NATIVE_AD -> nativeAdListener?.onUserRewarded(adUnit)
        }
    }

    override fun onRequestShow(adUnit: AdUnit) {
        when (adUnit.adType) {
            AdType.INTERSTITIAL -> interstitialListener?.onRequestShow(adUnit)
            AdType.REWARDED_VIDEO -> rewardedListener?.onRequestShow(adUnit)
            AdType.APP_OPEN_AD -> appOpenAdListener?.onRequestShow(adUnit)
            AdType.BANNER -> bannerListener?.onRequestShow(adUnit)
            AdType.NATIVE_AD -> nativeAdListener?.onRequestShow(adUnit)
        }
    }

    override fun onLoadSuccess(adUnit: AdUnit) {
        when (adUnit.adType) {
            AdType.INTERSTITIAL -> interstitialListener?.onLoadSuccess(adUnit)
            AdType.REWARDED_VIDEO -> rewardedListener?.onLoadSuccess(adUnit)
            AdType.APP_OPEN_AD -> appOpenAdListener?.onLoadSuccess(adUnit)
            AdType.BANNER -> bannerListener?.onLoadSuccess(adUnit)
            AdType.NATIVE_AD -> nativeAdListener?.onLoadSuccess(adUnit)
        }
    }

    override fun onLoadFail(adUnit: AdUnit, reason: AdStatus) {
        when (adUnit.adType) {
            AdType.INTERSTITIAL -> interstitialListener?.onLoadFail(adUnit, reason)
            AdType.REWARDED_VIDEO -> rewardedListener?.onLoadFail(adUnit, reason)
            AdType.APP_OPEN_AD -> appOpenAdListener?.onLoadFail(adUnit, reason)
            AdType.BANNER -> bannerListener?.onLoadFail(adUnit, reason)
            AdType.NATIVE_AD -> nativeAdListener?.onLoadFail(adUnit, reason)
        }
    }

    override fun onRequestLoad(adUnit: AdUnit) {
        when (adUnit.adType) {
            AdType.INTERSTITIAL -> interstitialListener?.onRequestLoad(adUnit)
            AdType.REWARDED_VIDEO -> rewardedListener?.onRequestLoad(adUnit)
            AdType.APP_OPEN_AD -> appOpenAdListener?.onRequestLoad(adUnit)
            AdType.BANNER -> bannerListener?.onRequestLoad(adUnit)
            AdType.NATIVE_AD -> nativeAdListener?.onRequestLoad(adUnit)
        }
    }


}