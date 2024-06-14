package com.roman.garden.mediation.admob

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.roman.garden.adbase.*
import com.roman.garden.adbase.AdError
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resume

internal class AdmobAdapter : Adapter() {

    override suspend fun initPlatform(
        activity: Activity?,
        platform: Platform,
        testMode: Boolean
    ): AdError? =
        suspendCancellableCoroutine {
            activity?.let { ac ->
                if (testMode) {
                    MobileAds.setRequestConfiguration(
                        RequestConfiguration.Builder()
                            .setTestDeviceIds(listOf("F3EDE78A2C3C4127A07CA5E97F0FDD02"))
                            .build()
                    )
                }
                MobileAds.initialize(ac.applicationContext) { _ ->
                    it.resume(null)
                }

            } ?: it.resume(AdError.ADAPTER_INIT_FAIL.zip("context can't be null"))
        }

    override fun loadBanner(adUnit: AdUnit) {
        getActivity()?.let { activity ->
            AdView(activity).apply {
                this.setAdSize(AdSize.BANNER)
                adUnit.adUnitId?.let {
                    this.adUnitId = it
                } ?: this.run {
                    onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("ad id can't be null"))
                    return
                }
                this.adListener = object : AdListener() {
                    override fun onAdClicked() {
                        // Code to be executed when the user clicks on an ad.
                        onAdClicked(adUnit)
                    }

                    override fun onAdClosed() {
                        // Code to be executed when the user is about to return
                        // to the app after tapping on an ad.
                        onAdClosed(adUnit)
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Code to be executed when an ad request fails.
                        onLoadFail(
                            adUnit,
                            AdError.AD_LOAD_FAIL.zip("${adError.code},${adError.message}")
                        )
                    }

                    override fun onAdImpression() {
                        // Code to be executed when an impression is recorded
                        // for an ad.
                    }

                    override fun onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                        onLoadSuccess(adUnit, this@apply)
                    }

                    override fun onAdOpened() {
                        // Code to be executed when an ad opens an overlay that
                        // covers the screen.
                        onAdShow(adUnit)
                    }
                }
                val request = AdRequest.Builder().build()
                this.loadAd(request)
            }
        } ?: onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("context can't be null"))
    }

    override fun showBanner(adUnit: AdUnit, viewGroup: ViewGroup?) {
        viewGroup?.let { container ->
            (getAdResponse(adUnit)?.obj as? AdView)?.let { adView ->
                container.visibility = View.VISIBLE
                container.removeAllViews()
                container.addView(adView)
            } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("response type error"))
        } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("container can't be null"))
    }

    override fun loadInterstitial(adUnit: AdUnit) {
        getActivity()?.let { activity ->
            adUnit.adUnitId?.let { id ->
                val request = AdRequest.Builder().build()
                InterstitialAd.load(
                    activity.applicationContext,
                    id,
                    request,
                    object : InterstitialAdLoadCallback() {

                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("${p0.code},${p0.message}"))
                        }

                        override fun onAdLoaded(p0: InterstitialAd) {
                            super.onAdLoaded(p0)
                            onLoadSuccess(adUnit, p0)
                        }
                    })
            } ?: onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("ad id can't be null"))
        } ?: onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("context can't be null"))
    }

    override fun showInterstitial(adUnit: AdUnit) {
        getActivity()?.let { activity ->
            (getAdResponse(adUnit)?.obj as? InterstitialAd)?.let { inter ->
                inter.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad.
                        onAdClicked(adUnit)
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        onAdClosed(adUnit)
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        onAdShowFail(
                            adUnit,
                            AdError.AD_SHOW_FAIL.zip("${p0.code},${p0.message}")
                        )
                    }

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        onAdShow(adUnit)
                    }
                }
                inter.show(activity)
            } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("invalid response"))
        } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("activity can't be null"))
    }

    override fun loadRewardedVideo(adUnit: AdUnit) {
        getActivity()?.let { activity ->
            adUnit.adUnitId?.let { id ->
                val request = AdRequest.Builder().build()
                RewardedAd.load(
                    activity.applicationContext,
                    id,
                    request,
                    object : RewardedAdLoadCallback() {
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("${p0.code},${p0.message}"))
                        }

                        override fun onAdLoaded(p0: RewardedAd) {
                            super.onAdLoaded(p0)
                            onLoadSuccess(adUnit, p0)
                        }
                    })
            } ?: onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("ad id can't be null"))
        } ?: onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("context can't be null"))
    }

    override fun showRewardedVideo(adUnit: AdUnit) {
        getActivity()?.let { activity ->
            (getAdResponse(adUnit)?.obj as? RewardedAd)?.let { rewarded ->
                var rewardedUser = false
                rewarded.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad.
                        onAdClicked(adUnit)
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        onAdClosed(adUnit, rewardedUser)
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        onAdShowFail(
                            adUnit,
                            AdError.AD_SHOW_FAIL.zip("${p0.code},${p0.message}")
                        )
                    }

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        onAdShow(adUnit)
                    }
                }
                rewarded.show(activity, OnUserEarnedRewardListener() {
                    rewardedUser = true
                    onUserRewarded(adUnit)
                })
            } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("invalid response"))
        } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("activity can't be null"))
    }

    override fun loadAppOpen(adUnit: AdUnit) {
        getActivity()?.let { activity ->
            adUnit.adUnitId?.let { id ->
                val request = AdRequest.Builder().build()
                AppOpenAd.load(
                    activity.applicationContext,
                    id,
                    request,
                    object : AppOpenAd.AppOpenAdLoadCallback() {

                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("${p0.code},${p0.message}"))
                        }

                        override fun onAdLoaded(p0: AppOpenAd) {
                            super.onAdLoaded(p0)
                            onLoadSuccess(adUnit, p0)
                        }
                    })
            } ?: onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("ad id can't be null"))
        } ?: onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("context can't be null"))
    }

    override fun showAppOpen(adUnit: AdUnit) {
        getActivity()?.let { activity ->
            (getAdResponse(adUnit)?.obj as? AppOpenAd)?.let { appOpenAd ->
                appOpenAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad.
                        onAdClicked(adUnit)
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        onAdClosed(adUnit)
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        onAdShowFail(
                            adUnit,
                            AdError.AD_SHOW_FAIL.zip("${p0.code},${p0.message}")
                        )
                    }

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        onAdShow(adUnit)
                    }
                }
                appOpenAd.show(activity)
            } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("invalid response"))
        } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("activity can't be null"))
    }

    override fun loadNative(adUnit: AdUnit) {
        getActivity()?.let { activity ->
            adUnit.adUnitId?.let { id ->
                val request = AdRequest.Builder().build()
                AdLoader.Builder(activity.applicationContext, id)
                    .forNativeAd { nativeAd ->
                        onLoadSuccess(adUnit, nativeAd)
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            onLoadFail(
                                adUnit,
                                AdError.AD_LOAD_FAIL.zip("${p0.code}, ${p0.message}")
                            )
                        }
                    }).build().loadAd(request)
            } ?: onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("ad id can't be null"))
        } ?: onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("context can't be null"))
    }

    override fun showNative(
        adUnit: AdUnit,
        viewGroup: ViewGroup?,
        size: Int,
        templateLayoutId: Int
    ) {
        viewGroup?.let { container ->
            (getAdResponse(adUnit)?.obj as? NativeAd)?.let { nativeAd ->
                var templateView: TemplateView? = null
                if (templateLayoutId > 0) {
                    templateView = LayoutInflater.from(container.context)
                        .inflate(templateLayoutId, null, false) as? TemplateView
                }
                if (templateView == null) {
                    templateView = when (size) {
                        NativeAdUtil.DEFAULT_SMALL_TEMPLATE -> LayoutInflater.from(container.context)
                            .inflate(R.layout.small_template, null, false) as? TemplateView
                        NativeAdUtil.DEFAULT_MEDIUM_TEMPLATE -> LayoutInflater.from(container.context)
                            .inflate(R.layout.medium_template, null, false) as? TemplateView
                        else -> null
                    }
                }
                templateView?.apply {
                    val styles = NativeTemplateStyle.Builder()
                        .withMainBackgroundColor(ColorDrawable(Color.WHITE)).build()
                    this.setStyles(styles)
                    this.setNativeAd(nativeAd)
                    container.visibility = View.VISIBLE
                    container.removeAllViews()
                    container.addView(this)
                } ?: onAdShowFail(
                    adUnit,
                    AdError.AD_SHOW_FAIL.zip("build native ad template error")
                )
            } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("invalid response"))
        } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("container can't be null"))
    }


}