package com.roman.garden.mediation.admob


import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.roman.garden.ad.base.*


class AdmobAdapter : Adapter() {

    override fun initSdk(
        context: Context,
        platform: Platform,
        initListener: IAdapterInitListener?
    ) {
        MobileAds.initialize(context) { _ ->
            initListener?.onInitSuccess()
        }
    }

    override fun realLoadInterstitial(context: Context, adImpl: AdImpl) {
        when(adImpl.isValid()){
            false -> notifyLoadFail(adImpl, AdStatus.AD_LOAD_FAIL.extra("AdImpl invalid:$adImpl"))
            true -> {
                InterstitialAd.load(context, adImpl.adUnit!!.adUnitId!!, AdRequest.Builder().build(), object :
                    InterstitialAdLoadCallback() {

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        notifyLoadFail(adImpl, AdStatus.AD_LOAD_FAIL.extra(p0.message))
                    }

                    override fun onAdLoaded(p0: InterstitialAd) {
                        super.onAdLoaded(p0)
                        adImpl.apply {
                            ad = p0
                            resetCacheTime()
                            notifyLoadSuccess(adImpl, AdStatus.AD_LOAD_SUCCESS.extra("admob interstitial"))
                        }
                    }
                })
            }
        }
    }

    override fun realLoadRewardedVideo(context: Context, adImpl: AdImpl) {
        when(adImpl.isValid()){
            false -> notifyLoadFail(adImpl, AdStatus.AD_LOAD_FAIL.extra("AdImpl invalid:$adImpl"))
            true -> {
                RewardedAd.load(context, adImpl.adUnit!!.adUnitId!!, AdRequest.Builder().build(), object : RewardedAdLoadCallback(){
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        notifyLoadFail(adImpl, AdStatus.AD_LOAD_FAIL.extra(p0.message))
                    }

                    override fun onAdLoaded(p0: RewardedAd) {
                        super.onAdLoaded(p0)
                        adImpl.apply {
                            ad = p0
                            resetCacheTime()
                            notifyLoadSuccess(this, AdStatus.AD_LOAD_SUCCESS.extra("admob rewarded"))
                        }
                    }
                })
            }
        }
    }

    override fun realLoadBanner(context: Context, adImpl: AdImpl) {
        when(adImpl.isValid()){
            false -> notifyLoadFail(adImpl, AdStatus.AD_LOAD_FAIL.extra("AdImple invalid:$adImpl"))
            true -> {
                var adView: AdView = AdView(context)
                adView.run {
                    setAdSize(AdSize.BANNER)
                    adListener = object : AdListener(){
                        override fun onAdClicked() {
                            super.onAdClicked()
                            notifyAdClicked(adImpl.adUnit, AdStatus.AD_CLICKED.extra("admob banner"))
                        }

                        override fun onAdClosed() {
                            super.onAdClosed()
                            notifyAdClose(adImpl.adUnit, AdStatus.AD_CLOSE.extra("admob banner"))
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()
                            notifyAdShow(adImpl.adUnit, AdStatus.AD_SHOW.extra("admob banner"))
                        }

                        override fun onAdLoaded() {
                            super.onAdLoaded()
                            adImpl.run {
                                ad = adView
                                resetCacheTime()
                                notifyLoadSuccess(this, AdStatus.AD_LOAD_SUCCESS.extra("admob banner"))
                            }
                        }

                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            notifyLoadFail(adImpl, AdStatus.AD_LOAD_FAIL.extra("admob banner"))
                        }
                    }
                    adUnitId = adImpl.adUnit!!.adUnitId!!
                    loadAd(AdRequest.Builder().build())
                }
            }
        }
    }

    override fun realLoadNative(context: Context, adImpl: AdImpl) {
        when(adImpl.isValid()){
            true -> {
                var adLoader = AdLoader.Builder(context, adImpl.adUnit!!.adUnitId!!)
                    .forNativeAd { nativeAd ->
                        val styles =
                            NativeTemplateStyle.Builder().withMainBackgroundColor(ColorDrawable(Color.WHITE))
                                .build()
                        with(LayoutInflater.from(context).inflate(R.layout.small_template, null, false)) {
                            (this as TemplateView).run {
                                setStyles(styles)
                                setNativeAd(nativeAd)
                                adImpl.let {
                                    it.ad = this
                                    it.resetCacheTime()
                                    notifyLoadSuccess(adImpl, AdStatus.AD_LOAD_SUCCESS.extra("admob native"))
                                }
                            }
                        }
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdLoaded() {
                            super.onAdLoaded()

                        }

                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            notifyLoadFail(adImpl, AdStatus.AD_LOAD_FAIL.extra("admob native"))
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
                            notifyAdClicked(adImpl.adUnit, AdStatus.AD_CLICKED.extra("admob native"))
                        }

                        override fun onAdClosed() {
                            super.onAdClosed()
                            notifyAdClose(adImpl.adUnit, AdStatus.AD_CLOSE.extra("admob native"))
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()
                            notifyAdShow(adImpl.adUnit, AdStatus.AD_SHOW.extra("admob native"))
                        }
                    })
                    .build()
                adLoader.loadAd(AdRequest.Builder().build())
            }
            false -> notifyLoadFail(adImpl, AdStatus.AD_LOAD_FAIL.extra("AdImpl invalid:$adImpl"))
        }
    }

    override fun realLoadAppOpenAd(context: Context, adImpl: AdImpl) {
        when(adImpl.isValid()){
            false -> notifyLoadFail(adImpl, AdStatus.AD_LOAD_FAIL.extra("AdImpl invalid:$adImpl"))
            true -> {
                AppOpenAd.load(context, adImpl.adUnit!!.adUnitId!!, AdRequest.Builder().build(), AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, object : AppOpenAd.AppOpenAdLoadCallback(){
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        notifyLoadFail(adImpl, AdStatus.AD_LOAD_FAIL.extra(p0.message))
                    }

                    override fun onAdLoaded(p0: AppOpenAd) {
                        super.onAdLoaded(p0)
                        adImpl.run {
                            ad = p0
                            resetCacheTime()
                            notifyLoadSuccess(this, AdStatus.AD_LOAD_SUCCESS.extra("admob appOpen"))
                        }
                    }
                })
            }
        }
    }

    override fun realShowInterstitial(activity: Activity, adImpl: AdImpl) {
       adImpl.apply {
           when(ad){
               is InterstitialAd -> {
                   (ad as InterstitialAd).run {
                       fullScreenContentCallback = object : FullScreenContentCallback() {
                           override fun onAdClicked() {
                               super.onAdClicked()
                               notifyAdClicked(adUnit, AdStatus.AD_CLICKED.extra("admob interstitial"))
                           }

                           override fun onAdShowedFullScreenContent() {
                               super.onAdShowedFullScreenContent()
                               notifyAdShow(adUnit, AdStatus.AD_SHOW.extra("admob interstitial"))
                           }

                           override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                               super.onAdFailedToShowFullScreenContent(p0)
                               notifyAdShowFail(adUnit, AdStatus.AD_SHOW_FAIL.extra("admob interstitial"))
                           }

                           override fun onAdDismissedFullScreenContent() {
                               super.onAdDismissedFullScreenContent()
                               notifyAdClose(adUnit, AdStatus.AD_CLOSE.extra("admob interstitial"))
                           }
                       }
                       show(activity)
                   }
               }
               else -> notifyAdShowFail(adImpl.adUnit, AdStatus.AD_SHOW_FAIL.extra("admob interstitial ad not exist"))
           }
       }
    }

    override fun realShowRewardedVideo(activity: Activity, adImpl: AdImpl) {
        adImpl.apply {
            when(ad){
                is RewardedAd -> {
                    (ad as RewardedAd).run {
                        fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                super.onAdClicked()
                                notifyAdClicked(adUnit, AdStatus.AD_CLICKED.extra("admob rewarded"))
                            }

                            override fun onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent()
                                notifyAdShow(adUnit, AdStatus.AD_SHOW.extra("admob rewarded"))
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                super.onAdFailedToShowFullScreenContent(p0)
                                notifyAdShowFail(adUnit, AdStatus.AD_SHOW_FAIL.extra("admob rewarded"))
                            }

                            override fun onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent()
                                notifyAdClose(adUnit, AdStatus.AD_CLOSE.extra("admob rewarded"))
                            }
                        }
                        show(activity) { _ ->
                            notifyUserRewarded(adImpl.adUnit, AdStatus.AD_REWARDED_USER.extra("admob rewarded"))
                        }
                    }
                }
                else -> notifyAdShowFail(adImpl.adUnit, AdStatus.AD_SHOW_FAIL.extra("admob rewarded ad not exist"))
            }
        }
    }

    override fun realShowBanner(adImpl: AdImpl, container: ViewGroup) {
       adImpl.apply {
           when(ad){
               is AdView -> {
                   (ad as AdView).run {
                       parent?.let { (it as ViewGroup).let { viewGroup -> viewGroup.removeAllViews() } }
                       container.addView(this)
                   }
               }
               else -> notifyAdShowFail(adImpl.adUnit, AdStatus.AD_SHOW_FAIL.extra("admob banner ad not exist"))
           }
       }
    }

    override fun realShowNative(adImpl: AdImpl, container: ViewGroup) {
        adImpl.apply {
            when(ad){
                is TemplateView -> {
                    (ad as TemplateView).run {
                        parent?.let { (it as ViewGroup).let { viewGroup -> viewGroup.removeAllViews() } }
                        container.addView(this)
                    }
                }
                else -> notifyAdShowFail(adImpl.adUnit, AdStatus.AD_SHOW_FAIL.extra("admob native ad not exist"))
            }
        }
    }

    override fun realShowAppOpenAd(activity: Activity, adImpl: AdImpl) {
        adImpl.apply {
            when(ad){
                is AppOpenAd -> {
                    (ad as AppOpenAd).run {
                        fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                super.onAdClicked()
                                notifyAdClicked(adUnit, AdStatus.AD_CLICKED.extra("admob AppOpenAd"))
                            }

                            override fun onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent()
                                notifyAdShow(adUnit, AdStatus.AD_SHOW.extra("admob AppOpenAd"))
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                super.onAdFailedToShowFullScreenContent(p0)
                                notifyAdShowFail(adUnit, AdStatus.AD_SHOW_FAIL.extra("admob AppOpenAd"))
                            }

                            override fun onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent()
                                notifyAdClose(adUnit, AdStatus.AD_CLOSE.extra("admob AppOpenAd"))
                            }
                        }
                        this.show(activity)
                    }
                }
                else -> notifyAdShowFail(adImpl.adUnit, AdStatus.AD_SHOW_FAIL.extra("admob appOpen ad not exist"))
            }
        }
    }


}