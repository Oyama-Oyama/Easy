package com.roman.garden.mediation.vungle

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.roman.garden.adbase.AdError
import com.roman.garden.adbase.AdUnit
import com.roman.garden.adbase.Adapter
import com.roman.garden.adbase.Platform
import com.vungle.warren.*
import com.vungle.warren.error.VungleException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal class VungleAdapter : Adapter() {
    override suspend fun initPlatform(
        activity: Activity?,
        platform: Platform,
        testMode: Boolean
    ): AdError? = suspendCancellableCoroutine {
        activity?.let { ac ->
            platform.appId?.let { id ->
                val settings = VungleSettings.Builder().setAndroidIdOptOut(true).build()
                Vungle.init(id, ac.applicationContext, object : InitCallback {
                    override fun onSuccess() {
                        it.resume(null)
                    }

                    override fun onError(p0: VungleException?) {
                        it.resume(AdError.ADAPTER_INIT_FAIL.zip("${p0?.message}"))
                    }

                    override fun onAutoCacheAdAvailable(p0: String?) {
                    }

                }, settings)
            } ?: it.resume(AdError.ADAPTER_INIT_FAIL.zip("vungle appId invalid"))
        } ?: it.resume(AdError.ADAPTER_INIT_FAIL.zip("context can't be null"))
    }

    override fun loadBanner(adUnit: AdUnit) {
        if (!Vungle.isInitialized()) {
            onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("vungle not inited"))
            return
        }
        adUnit.adUnitId?.let { adId ->
            val bannerAdConfig = BannerAdConfig()
            bannerAdConfig.adSize = AdConfig.AdSize.BANNER
            Banners.loadBanner(adId, bannerAdConfig, object : LoadAdCallback {
                override fun onAdLoad(p0: String?) {
                    onLoadSuccess(adUnit, p0)
                }

                override fun onError(p0: String?, p1: VungleException?) {
                    onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("${p1?.message}"))
                }

            })
        } ?: onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("adUnitId can't be null"))
    }

    override fun showBanner(adUnit: AdUnit, viewGroup: ViewGroup?) {
        (getAdResponse(adUnit)?.obj as? String)?.let { placementId ->
            val bannerAdConfig = BannerAdConfig()
            bannerAdConfig.adSize = AdConfig.AdSize.BANNER
            when (Banners.canPlayAd(placementId, bannerAdConfig.adSize)) {
                true -> {
                    val vungleBanner =
                        Banners.getBanner(placementId, bannerAdConfig, object : PlayAdCallback {
                            override fun creativeId(p0: String?) {

                            }

                            override fun onAdStart(p0: String?) {
                                onAdShow(adUnit)
                            }

                            override fun onAdEnd(p0: String?, p1: Boolean, p2: Boolean) {

                            }

                            override fun onAdEnd(p0: String?) {
                                onAdClosed(adUnit, false)
                            }

                            override fun onAdClick(p0: String?) {
                                onAdClicked(adUnit)
                            }

                            override fun onAdRewarded(p0: String?) {

                            }

                            override fun onAdLeftApplication(p0: String?) {

                            }

                            override fun onError(p0: String?, p1: VungleException?) {
                                onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("${p1?.message}"))
                            }

                            override fun onAdViewed(p0: String?) {

                            }

                        })
                    vungleBanner?.let { banner ->
                        viewGroup?.let { container ->
                            container.visibility = View.VISIBLE
                            container.removeAllViews()
                            container.addView(banner)
                        } ?: onAdShowFail(
                            adUnit,
                            AdError.AD_SHOW_FAIL.zip("container can't be null")
                        )
                    } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("ad response invalid"))
                }
                else -> onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("vungle ad not ready"))
            }
        } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("invalid response"))
    }

    override fun loadInterstitial(adUnit: AdUnit) {
        if (!Vungle.isInitialized()) {
            onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("vungle not inited"))
            return
        }
        adUnit.adUnitId?.let { adId ->
            Vungle.loadAd(adId, null, object : LoadAdCallback {
                override fun onAdLoad(p0: String?) {
                    onLoadSuccess(adUnit, p0)
                }

                override fun onError(p0: String?, p1: VungleException?) {
                    onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("${p1?.message}"))
                }
            })
        } ?: onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("adUnitId can't be null"))
    }

    override fun showInterstitial(adUnit: AdUnit) {
        (getAdResponse(adUnit)?.obj as? String)?.let { placementId ->
            when (Vungle.canPlayAd(placementId)) {
                true -> {
                    Vungle.playAd(placementId, null, object : PlayAdCallback {
                        override fun creativeId(p0: String?) {

                        }

                        override fun onAdStart(p0: String?) {
                            onAdShow(adUnit)
                        }

                        override fun onAdEnd(p0: String?, p1: Boolean, p2: Boolean) {

                        }

                        override fun onAdEnd(p0: String?) {
                            onAdClosed(adUnit, false)
                        }

                        override fun onAdClick(p0: String?) {
                            onAdClicked(adUnit)
                        }

                        override fun onAdRewarded(p0: String?) {
                            // onUserRewarded(adUnit)
                        }

                        override fun onAdLeftApplication(p0: String?) {

                        }

                        override fun onError(p0: String?, p1: VungleException?) {
                            onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("${p1?.message}"))
                        }

                        override fun onAdViewed(p0: String?) {

                        }

                    })
                }
                else -> onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("vungle ad not ready"))
            }
        } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("invalid response"))
    }

    override fun loadRewardedVideo(adUnit: AdUnit) {
        if (!Vungle.isInitialized()) {
            onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("vungle not inited"))
            return
        }
        adUnit.adUnitId?.let { adId ->
            Vungle.loadAd(adId, null, object : LoadAdCallback {
                override fun onAdLoad(p0: String?) {
                    onLoadSuccess(adUnit, p0)
                }

                override fun onError(p0: String?, p1: VungleException?) {
                    onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("${p1?.message}"))
                }
            })
        } ?: onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("adUnitId can't be null"))
    }

    override fun showRewardedVideo(adUnit: AdUnit) {
        (getAdResponse(adUnit)?.obj as? String)?.let { placementId ->
            when (Vungle.canPlayAd(placementId)) {
                true -> {
                    var rewardedUser = false
                    Vungle.playAd(placementId, null, object : PlayAdCallback {
                        override fun creativeId(p0: String?) {

                        }

                        override fun onAdStart(p0: String?) {
                            onAdShow(adUnit)
                        }

                        override fun onAdEnd(p0: String?, p1: Boolean, p2: Boolean) {

                        }

                        override fun onAdEnd(p0: String?) {
                            onAdClosed(adUnit, rewardedUser)
                        }

                        override fun onAdClick(p0: String?) {
                            onAdClicked(adUnit)
                        }

                        override fun onAdRewarded(p0: String?) {
                            rewardedUser = true
                            onUserRewarded(adUnit)
                        }

                        override fun onAdLeftApplication(p0: String?) {

                        }

                        override fun onError(p0: String?, p1: VungleException?) {
                            onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("${p1?.message}"))
                        }

                        override fun onAdViewed(p0: String?) {

                        }

                    })
                }
                else -> onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("vungle ad not ready"))
            }
        } ?: onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("invalid response"))
    }

    override fun loadAppOpen(adUnit: AdUnit) {
        onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("vungle unsupported"))
    }

    override fun showAppOpen(adUnit: AdUnit) {
        onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("vungle unsupported"))
    }

    override fun loadNative(adUnit: AdUnit) {
        onLoadFail(adUnit, AdError.AD_LOAD_FAIL.zip("vungle unsupported"))
    }

    override fun showNative(
        adUnit: AdUnit,
        viewGroup: ViewGroup?,
        size: Int,
        templateLayoutId: Int
    ) {
        onAdShowFail(adUnit, AdError.AD_SHOW_FAIL.zip("vungle unsupported"))
    }
}