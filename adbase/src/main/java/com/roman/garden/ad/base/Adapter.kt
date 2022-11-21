package com.roman.garden.ad.base

import android.app.Activity
import android.content.Context
import android.view.ViewGroup


abstract class Adapter {

    private var tasks: MutableList<AdImpl> = mutableListOf()
    private var adLoadListener: IAdLoadListener? = null
    private var adListener: IAdListener? = null

    fun initAdapter(context: Context, platform: Platform, initListener: IAdapterInitListener?) {
        initSdk(context, platform, initListener)
    }

    fun load(context: Context, adUnit: AdUnit) {
        val (status, adImpl) = isOkToLoad(adUnit)
        when (status) {
            true -> {
                notifyRequestLoad(adUnit, AdStatus.AD_REQUEST_LOAD)
                when (adUnit.adType) {
                    AdType.INTERSTITIAL -> realLoadInterstitial(context, adImpl)
                    AdType.REWARDED_VIDEO -> realLoadRewardedVideo(context, adImpl)
                    AdType.BANNER -> realLoadBanner(context, adImpl)
                    AdType.NATIVE_AD -> realLoadNative(context, adImpl)
                    AdType.APP_OPEN_AD -> realLoadAppOpenAd(context, adImpl)
                    else -> Logger.d("unable find ad type t load:$adUnit")
                }
            }
            false -> Logger.d("already in loading:$adUnit")
        }
    }

    fun showInterstitial(activity: Activity, adUnit: AdUnit) {
        findAdImpl(adUnit).run {
            when (this) {
                null -> notifyAdShowFail(adUnit, AdStatus.AD_SHOW_FAIL.extra("ad does not exist"))
                else -> {
                    notifyRequestShow(adUnit, AdStatus.AD_REQUEST_SHOW)
                    realShowInterstitial(activity, this)
                    this.let {
                        tasks.remove(it)
                    }
                }
            }
        }
    }

    fun showRewardedVideo(activity: Activity, adUnit: AdUnit) {
        findAdImpl(adUnit).run {
            when (this) {
                null -> notifyAdShowFail(adUnit, AdStatus.AD_SHOW_FAIL.extra("ad does not exist"))
                else -> {
                    notifyRequestShow(adUnit, AdStatus.AD_REQUEST_SHOW)
                    realShowRewardedVideo(activity, this)
                    this.let {
                        tasks.remove(it)
                    }
                }
            }
        }
    }

    fun showBanner(adUnit: AdUnit, container: ViewGroup) {
        findAdImpl(adUnit).run {
            when (this) {
                null -> notifyAdShowFail(adUnit, AdStatus.AD_SHOW_FAIL.extra("ad does not exist"))
                else -> {
                    notifyRequestShow(adUnit, AdStatus.AD_REQUEST_SHOW)
                    realShowBanner(this, container)
                    this.let {
                        tasks.remove(it)
                    }
                }
            }
        }
    }

    fun showNative(adUnit: AdUnit, container: ViewGroup) {
        findAdImpl(adUnit).run {
            when (this) {
                null -> notifyAdShowFail(adUnit, AdStatus.AD_SHOW_FAIL.extra("ad does not exist"))
                else -> {
                    notifyRequestShow(adUnit, AdStatus.AD_REQUEST_SHOW)
                    realShowNative(this, container)
                    this.let {
                        tasks.remove(it)
                    }
                }
            }
        }
    }

    fun showAppOpenAd(activity: Activity, adUnit: AdUnit) {
        findAdImpl(adUnit).run {
            when (this) {
                null -> notifyAdShowFail(adUnit, AdStatus.AD_SHOW_FAIL.extra("ad does not exist"))
                else -> {
                    notifyRequestShow(adUnit, AdStatus.AD_REQUEST_SHOW)
                    realShowAppOpenAd(activity, this)
                    this.let {
                        tasks.remove(it)
                    }
                }
            }
        }
    }

    fun setAdLoadListener(listener: IAdLoadListener?) {
        adLoadListener = listener
    }

    fun setAdListener(listener: IAdListener?) {
        adListener = listener
    }

    protected fun notifyRequestLoad(adUnit: AdUnit, status: AdStatus) {
        Logger.d(status.extra(adUnit.toString()).toString())
        adLoadListener?.onRequestLoad(adUnit)
    }

    protected fun notifyLoadSuccess(adImpl: AdImpl, status: AdStatus) {
        adImpl.adUnit.toString().let { status.extra(it).toString().let { d -> Logger.d(d) } }
        adLoadListener?.onLoadSuccess(adImpl.adUnit)
    }

    protected fun notifyLoadFail(adImpl: AdImpl, status: AdStatus) {
        adImpl.adUnit.toString().let { status.extra(it).toString().let { d -> Logger.d(d) } }
        adLoadListener?.onLoadFail(adImpl.adUnit, status)
        tasks.let { tasks.remove(adImpl) }
    }

    protected fun notifyAdShow(adUnit: AdUnit, status: AdStatus) {
        Logger.d(status.extra(adUnit.toString()).toString())
        adListener?.onAdShow(adUnit)
    }

    protected fun notifyAdShowFail(adUnit: AdUnit, status: AdStatus) {
        Logger.d(status.extra(adUnit.toString()).toString())
        adListener?.onAdShowFail(adUnit, status)
    }

    protected fun notifyAdClicked(adUnit: AdUnit, status: AdStatus) {
        Logger.d(status.extra(adUnit.toString()).toString())
        adListener?.onAdClicked(adUnit)
    }

    protected fun notifyAdClose(adUnit: AdUnit, status: AdStatus) {
        Logger.d(status.extra(adUnit.toString()).toString())
        adListener?.onAdClosed(adUnit)
    }

    protected fun notifyUserRewarded(adUnit: AdUnit, status: AdStatus) {
        Logger.d(status.extra(adUnit.toString()).toString())
        adListener?.onUserRewarded(adUnit)
    }

    protected fun notifyRequestShow(adUnit: AdUnit, status: AdStatus) {
        Logger.d(status.extra(adUnit.toString()).toString())
        adListener?.onRequestShow(adUnit)
    }

    private fun findAdImpl(adUnit: AdUnit): AdImpl? {
        return tasks.find { adImpl ->
            adImpl.adUnit.isEqualTo(adUnit)
        }
    }

    private fun isOkToLoad(adUnit: AdUnit): Pair<Boolean, AdImpl> {
        return tasks.run {
            this.find { adImpl ->
                adImpl.adUnit.isEqualTo(adUnit)
            }?.run {
                when (ad) {
                    null -> {
                        when (isValidLoad()) {
                            true -> return Pair(false, this)
                            false -> {
                                resetLoadTime()
                                this.adUnit = adUnit
                                return Pair(true, this)
                            }
                        }
                    }
                    else -> {
                        when (isValidCache()) {
                            true -> return Pair(false, this)
                            false -> {
                                resetCacheTime()
                                ad = null
                                this.adUnit = adUnit
                                return Pair(true, this)
                            }
                        }
                    }
                }
            }
            var adImpl = AdImpl().apply {
                resetLoadTime()
                this.adUnit = adUnit
            }
            tasks.add(adImpl)
            return Pair(true, adImpl)
        }
    }

    fun isValidAd(adUnit: AdUnit?) : Boolean {
        return adUnit?.let {
            findAdImpl(it)?.isValidCache()
        } == true
    }

    protected abstract fun initSdk(
        context: Context,
        platform: Platform,
        initListener: IAdapterInitListener?
    )

    protected abstract fun realLoadInterstitial(context: Context, adImpl: AdImpl)

    protected abstract fun realLoadRewardedVideo(context: Context, adImpl: AdImpl)

    protected abstract fun realLoadBanner(context: Context, adImpl: AdImpl)

    protected abstract fun realLoadNative(context: Context, adImpl: AdImpl)

    protected abstract fun realLoadAppOpenAd(context: Context, adImpl: AdImpl)

    protected abstract fun realShowInterstitial(activity: Activity, adImpl: AdImpl)

    protected abstract fun realShowRewardedVideo(activity: Activity, adImpl: AdImpl)

    protected abstract fun realShowBanner(adImpl: AdImpl, container: ViewGroup)

    protected abstract fun realShowNative(adImpl: AdImpl, container: ViewGroup)

    protected abstract fun realShowAppOpenAd(activity: Activity, adImpl: AdImpl)

}