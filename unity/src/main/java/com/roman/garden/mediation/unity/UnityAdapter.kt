package com.roman.garden.mediation.unity

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import com.roman.garden.ad.base.*
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

class UnityAdapter : Adapter() {

    override fun initSdk(
        context: Context,
        platform: Platform,
        initListener: IAdapterInitListener?
    ) {
        UnityAds.initialize(
            context,
            platform.appId,
            true,
            object : IUnityAdsInitializationListener {
                override fun onInitializationComplete() {
                    initListener?.onInitSuccess()
                }

                override fun onInitializationFailed(
                    error: UnityAds.UnityAdsInitializationError?,
                    message: String?
                ) {
                    initListener?.onInitFail(AdStatus.AD_ADAPTER_INIT_FAIL.extra("error:$error;message:$message"))
                }
            })
    }

    override fun realLoadInterstitial(context: Context, adImpl: AdImpl) {
        when (adImpl.isValid()) {
            false -> notifyLoadFail(adImpl, AdStatus.AD_LOAD_FAIL.extra("AdImpl invalid:$adImpl"))
            true -> {
                UnityAds.load(adImpl.adUnit.adUnitId, object : IUnityAdsLoadListener {
                    override fun onUnityAdsAdLoaded(placementId: String?) {
                        adImpl.apply {
                            ad = placementId
                            resetCacheTime()
                            notifyLoadSuccess(
                                this,
                                AdStatus.AD_LOAD_SUCCESS.extra("unity interstitial")
                            )
                        }
                    }

                    override fun onUnityAdsFailedToLoad(
                        placementId: String?,
                        error: UnityAds.UnityAdsLoadError?,
                        message: String?
                    ) {
                        notifyLoadFail(
                            adImpl,
                            AdStatus.AD_LOAD_FAIL.extra("placement:$placementId;error:$error;message:$message")
                        )
                    }
                })
            }
        }
    }

    override fun realLoadRewardedVideo(context: Context, adImpl: AdImpl) {
        when (adImpl.isValid()) {
            false -> notifyLoadFail(adImpl, AdStatus.AD_LOAD_FAIL.extra("AdImpl invalid:$adImpl"))
            true -> {
                UnityAds.load(adImpl.adUnit.adUnitId, object : IUnityAdsLoadListener {
                    override fun onUnityAdsAdLoaded(placementId: String?) {
                        adImpl.apply {
                            ad = placementId
                            resetCacheTime()
                            notifyLoadSuccess(
                                this,
                                AdStatus.AD_LOAD_SUCCESS.extra("unity rewarded")
                            )
                        }
                    }

                    override fun onUnityAdsFailedToLoad(
                        placementId: String?,
                        error: UnityAds.UnityAdsLoadError?,
                        message: String?
                    ) {
                        notifyLoadFail(
                            adImpl,
                            AdStatus.AD_LOAD_FAIL.extra("placement:$placementId;error:$error;message:$message")
                        )
                    }
                })
            }
        }
    }

    override fun realLoadBanner(context: Context, adImpl: AdImpl) {
        when (adImpl.isValid()) {
            false -> notifyLoadFail(adImpl, AdStatus.AD_LOAD_FAIL.extra("AdImpl invalid:$adImpl"))
            true -> {
                ActivityStatus.instance.activity.let {
                    if (it == null) {
                        notifyLoadFail(
                            adImpl,
                            AdStatus.AD_LOAD_FAIL.extra("unity banner: activity is null")
                        )
                    } else {
                        var bannerView =
                            BannerView(it, adImpl.adUnit.adUnitId, UnityBannerSize(320, 50))
                        bannerView.listener = object : BannerView.IListener {
                            override fun onBannerLoaded(bannerAdView: BannerView?) {
                                adImpl.apply {
                                    ad = bannerAdView
                                    resetCacheTime()
                                    notifyLoadSuccess(
                                        adImpl,
                                        AdStatus.AD_LOAD_SUCCESS.extra("unity banner")
                                    )
                                }
                            }

                            override fun onBannerClick(bannerAdView: BannerView?) {
                                notifyAdClicked(
                                    adImpl.adUnit,
                                    AdStatus.AD_CLICKED.extra("unity banner")
                                )
                            }

                            override fun onBannerFailedToLoad(
                                bannerAdView: BannerView?,
                                errorInfo: BannerErrorInfo?
                            ) {
                                notifyLoadFail(
                                    adImpl,
                                    AdStatus.AD_LOAD_FAIL.extra("unity banner:$errorInfo")
                                )
                            }

                            override fun onBannerLeftApplication(bannerView: BannerView?) {

                            }
                        }
                        bannerView.load()
                    }
                }
            }
        }
    }

    override fun realLoadNative(context: Context, adImpl: AdImpl) {
        notifyLoadFail(adImpl, AdStatus.AD_LOAD_FAIL.extra("not support unity native"))
    }

    override fun realLoadAppOpenAd(context: Context, adImpl: AdImpl) {
        notifyLoadFail(adImpl, AdStatus.AD_LOAD_FAIL.extra("not support unity app open ad"))
    }

    override fun realShowInterstitial(activity: Activity, adImpl: AdImpl) {
        adImpl.apply {
            when (ad) {
                is String -> {
                    (ad as String).run {
                        UnityAds.show(activity, this, object : IUnityAdsShowListener {
                            override fun onUnityAdsShowFailure(
                                placementId: String?,
                                error: UnityAds.UnityAdsShowError?,
                                message: String?
                            ) {
                                notifyAdShowFail(
                                    adImpl.adUnit,
                                    AdStatus.AD_SHOW_FAIL.extra("unity interstitial placement:$placementId;error:$error;message:$message")
                                )
                            }

                            override fun onUnityAdsShowStart(placementId: String?) {
                                notifyAdShow(
                                    adImpl.adUnit,
                                    AdStatus.AD_SHOW.extra("unity interstitial:$placementId")
                                )
                            }

                            override fun onUnityAdsShowClick(placementId: String?) {
                                notifyAdClicked(
                                    adImpl.adUnit,
                                    AdStatus.AD_CLICKED.extra("unity interstitial:$placementId")
                                )
                            }

                            override fun onUnityAdsShowComplete(
                                placementId: String?,
                                state: UnityAds.UnityAdsShowCompletionState?
                            ) {
                                notifyAdClose(
                                    adImpl.adUnit,
                                    AdStatus.AD_CLOSE.extra("unity interstitial placement:$placementId;state:$state")
                                )
                            }
                        })
                    }
                }
                else -> notifyAdShowFail(
                    adImpl.adUnit,
                    AdStatus.AD_SHOW_FAIL.extra("unity interstitial ad not exist")
                )
            }
        }
    }

    override fun realShowRewardedVideo(activity: Activity, adImpl: AdImpl) {
        adImpl.apply {
            when (ad) {
                is String -> {
                    (ad as String).run {
                        UnityAds.show(activity, this, object : IUnityAdsShowListener {
                            override fun onUnityAdsShowFailure(
                                placementId: String?,
                                error: UnityAds.UnityAdsShowError?,
                                message: String?
                            ) {
                                notifyAdShowFail(
                                    adImpl.adUnit,
                                    AdStatus.AD_SHOW_FAIL.extra("unity rewarded placement:$placementId;error:$error;message:$message")
                                )
                            }

                            override fun onUnityAdsShowStart(placementId: String?) {
                                notifyAdShow(
                                    adImpl.adUnit,
                                    AdStatus.AD_SHOW.extra("unity rewarded:$placementId")
                                )
                            }

                            override fun onUnityAdsShowClick(placementId: String?) {
                                notifyAdClicked(
                                    adImpl.adUnit,
                                    AdStatus.AD_CLICKED.extra("unity rewarded:$placementId")
                                )
                            }

                            override fun onUnityAdsShowComplete(
                                placementId: String?,
                                state: UnityAds.UnityAdsShowCompletionState?
                            ) {
                                if (state != UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                                    notifyUserRewarded(
                                        adImpl.adUnit,
                                        AdStatus.AD_REWARDED_USER.extra("unity rewarded placement:$placementId;state:$state")
                                    )
                                }
                                notifyAdClose(
                                    adImpl.adUnit,
                                    AdStatus.AD_CLOSE.extra("unity rewarded placement:$placementId;state:$state")
                                )
                            }
                        })
                    }
                }
                else -> notifyAdShowFail(
                    adImpl.adUnit,
                    AdStatus.AD_SHOW_FAIL.extra("unity rewarded ad not exist")
                )
            }
        }
    }

    override fun realShowBanner(adImpl: AdImpl, container: ViewGroup) {
        adImpl.apply {
            when (ad) {
                is BannerView -> {
                    (ad as BannerView).run {
                        parent?.let { (it as ViewGroup).let { viewGroup -> viewGroup.removeAllViews() } }
                        container.addView(this)
                    }
                }
                else -> notifyAdShowFail(
                    adImpl.adUnit,
                    AdStatus.AD_SHOW_FAIL.extra("unity rewarded ad not exist")
                )
            }
        }
    }

    override fun realShowNative(adImpl: AdImpl, container: ViewGroup) {
        notifyAdShowFail(adImpl.adUnit, AdStatus.AD_SHOW_FAIL.extra("unity nt support native ad"))
    }

    override fun realShowAppOpenAd(activity: Activity, adImpl: AdImpl) {
        notifyAdShowFail(adImpl.adUnit, AdStatus.AD_SHOW_FAIL.extra("unity nt support app open ad"))
    }
}