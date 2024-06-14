package com.roman.garden.mediation.unity

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.roman.garden.adbase.AdError
import com.roman.garden.adbase.AdUnit
import com.roman.garden.adbase.Adapter
import com.roman.garden.adbase.Platform
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal class UnityAdapter : Adapter() {

    override suspend fun initPlatform(
        activity: Activity?,
        platform: Platform,
        testMode: Boolean
    ): AdError? =
        suspendCancellableCoroutine {
            activity?.let { ac ->
                UnityAds.initialize(
                    ac.applicationContext,
                    platform.appId,
                    testMode,
                    object : IUnityAdsInitializationListener {
                        override fun onInitializationComplete() {
                            it.resume(null)
                        }

                        override fun onInitializationFailed(
                            error: UnityAds.UnityAdsInitializationError?,
                            message: String?
                        ) {
                            it.resume(AdError.ADAPTER_INIT_FAIL.zip("$message"))
                        }
                    })
            } ?: it.resume(AdError.ADAPTER_INIT_FAIL.zip("context can't be null"))

        }

    override fun loadBanner(adUnit: AdUnit) {
        getActivity()?.let { activity ->
            adUnit.adUnitId?.let { adId ->
                BannerView(activity, adId, UnityBannerSize(320, 50)).apply {
                    this.listener = object : BannerView.Listener() {
                        override fun onBannerLoaded(bannerAdView: BannerView?) {
                            onLoadSuccess(adUnit, bannerAdView)
                        }

                        override fun onBannerFailedToLoad(
                            bannerAdView: BannerView?,
                            errorInfo: BannerErrorInfo?
                        ) {
                            onLoadFail(
                                adUnit,
                                AdError.AD_LOAD_FAIL.zip("${errorInfo?.errorCode},${errorInfo?.errorMessage}")
                            )
                        }

                        override fun onBannerClick(bannerAdView: BannerView?) {
                            onAdClosed(adUnit)
                        }

                        override fun onBannerLeftApplication(bannerAdView: BannerView?) {}
                    }
                    this.load()
                }
            } ?: onLoadFail(adUnit, AdError.AD_SHOW_FAIL.zip("adUnitId can't be null"))
        } ?: onLoadFail(adUnit, AdError.AD_SHOW_FAIL.zip("context can't be null"))
    }

    override fun showBanner(adUnit: AdUnit, viewGroup: ViewGroup?) {
        viewGroup?.let { container ->
            (getAdResponse(adUnit)?.obj as? BannerView)?.let { adView ->
                container.visibility = View.VISIBLE
                container.removeAllViews()
                container.addView(adView)
            } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("invalid response"))
        } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("container can't be null"))
    }

    override fun loadInterstitial(adUnit: AdUnit) {
        adUnit.adUnitId?.let { adId ->
            UnityAds.load(adId, object : IUnityAdsLoadListener {
                override fun onUnityAdsAdLoaded(placementId: String?) {
                    onLoadSuccess(adUnit, placementId)
                }

                override fun onUnityAdsFailedToLoad(
                    placementId: String?,
                    error: UnityAds.UnityAdsLoadError?,
                    message: String?
                ) {
                    onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("$message"))
                }
            })
        } ?: onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("adUnitId can't be null"))
    }

    override fun showInterstitial(adUnit: AdUnit) {
        getActivity()?.let { activity ->
            (getAdResponse(adUnit)?.obj as? String)?.let { placementId ->
                UnityAds.show(activity, placementId, object : IUnityAdsShowListener {
                    override fun onUnityAdsShowFailure(
                        placementId: String?,
                        error: UnityAds.UnityAdsShowError?,
                        message: String?
                    ) {
                        onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("$message"))
                    }

                    override fun onUnityAdsShowStart(placementId: String?) {
                        onAdShow(adUnit)
                    }

                    override fun onUnityAdsShowClick(placementId: String?) {
                        onAdClicked(adUnit)
                    }

                    override fun onUnityAdsShowComplete(
                        placementId: String?,
                        state: UnityAds.UnityAdsShowCompletionState?
                    ) {
                        onAdClosed(adUnit)
                    }
                })
            } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("invalid response"))
        } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("context can't be null"))
    }

    override fun loadRewardedVideo(adUnit: AdUnit) {
        adUnit.adUnitId?.let { adId ->
            UnityAds.load(adId, object : IUnityAdsLoadListener {
                override fun onUnityAdsAdLoaded(placementId: String?) {
                    onLoadSuccess(adUnit, placementId)
                }

                override fun onUnityAdsFailedToLoad(
                    placementId: String?,
                    error: UnityAds.UnityAdsLoadError?,
                    message: String?
                ) {
                    onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("$message"))
                }
            })
        } ?: onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("adUnitId can't be null"))
    }

    override fun showRewardedVideo(adUnit: AdUnit) {
        getActivity()?.let { activity ->
            (getAdResponse(adUnit)?.obj as? String)?.let { placementId ->
                UnityAds.show(activity, placementId, object : IUnityAdsShowListener {
                    override fun onUnityAdsShowFailure(
                        placementId: String?,
                        error: UnityAds.UnityAdsShowError?,
                        message: String?
                    ) {
                        onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("$message"))
                    }

                    override fun onUnityAdsShowStart(placementId: String?) {
                        onAdShow(adUnit)
                    }

                    override fun onUnityAdsShowClick(placementId: String?) {
                        onAdClicked(adUnit)
                    }

                    override fun onUnityAdsShowComplete(
                        placementId: String?,
                        state: UnityAds.UnityAdsShowCompletionState?
                    ) {
                        var rewardedUser = false
                        if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                            rewardedUser = true
                            onUserRewarded(adUnit)
                        }
                        onAdClosed(adUnit, rewardedUser)
                    }
                })
            } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("invalid response"))
        } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("context can't be null"))
    }

    override fun loadAppOpen(adUnit: AdUnit) {
        onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("unsupported"))
    }

    override fun showAppOpen(adUnit: AdUnit) {
        onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("unsupported"))
    }

    override fun loadNative(adUnit: AdUnit) {
        onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("unsupported"))
    }

    override fun showNative(
        adUnit: AdUnit,
        viewGroup: ViewGroup?,
        size: Int,
        templateLayoutId: Int
    ) {
        onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("unsupported"))
    }

}