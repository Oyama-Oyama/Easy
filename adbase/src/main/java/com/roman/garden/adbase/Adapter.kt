package com.roman.garden.adbase

import android.app.Activity
import android.view.ViewGroup


abstract class Adapter {

    protected var loadCallback: IAdLoadCallback? = null
    protected var adCallback: IAdCallback? = null
    protected var adapterCallback: IAdapterCallback? = null

    protected var cacheMap = mutableMapOf<AdUnit, AdResponse>()

    fun destroy() {
        setAdCallback(null)
        setAdLoadCallback(null)
        setAdapterCallback(null)
        cacheMap.clear()
    }

    /**公共信息*/
    fun setAdapterCallback(callback: IAdapterCallback?) = callback.also { adapterCallback = it }

    /**广告加载事件*/
    fun setAdLoadCallback(callback: IAdLoadCallback?) = callback.also { loadCallback = it }

    /**广告事件*/
    fun setAdCallback(callback: IAdCallback?) = callback.also { adCallback = it }

    /**
     *  加载广告类型分发
     */
    fun load(adUnit: AdUnit) {
        onRequestLoad(adUnit)
        val loadStatus = cacheMap[adUnit]?.let {
            it.obj?.let { _ ->
                it.isValidCache()
            } ?: it.isValidLoad()
        } ?: false

        if (loadStatus) return
        else cacheMap.remove(adUnit)

        var response = AdResponse()
        response.startLoadTime = System.currentTimeMillis()
        cacheMap[adUnit] = response

        when (adUnit.adType) {
            AdType.APP_OPEN_AD -> loadAppOpen(adUnit)
            AdType.INTERSTITIAL -> loadInterstitial(adUnit)
            AdType.REWARDED_VIDEO -> loadRewardedVideo(adUnit)
            AdType.BANNER -> loadBanner(adUnit)
            AdType.NATIVE_AD -> loadNative(adUnit)
            else -> {
                loadCallback?.let { it.onLoadFail(adUnit, AdError.INVALID_AD_TYPE) }
            }
        }
    }

    /**
     * 是否存在有效缓存广告
     */
    fun isValidCache(adUnit: AdUnit): Boolean =
        cacheMap[adUnit]?.let { it.obj != null && it.isValidCache() } == true

    /**
     *  展示广告类型分发
     */
    fun show(
        adUnit: AdUnit, viewGroup: ViewGroup?, size: Int = 1,
        templateLayoutId: Int = -1
    ) {
        onRequestShow(adUnit)
        when (adUnit.adType) {
            AdType.APP_OPEN_AD -> showAppOpen(adUnit)
            AdType.INTERSTITIAL -> showInterstitial(adUnit)
            AdType.REWARDED_VIDEO -> showRewardedVideo(adUnit)
            AdType.BANNER -> showBanner(adUnit, viewGroup)
            AdType.NATIVE_AD -> showNative(adUnit, viewGroup, size, templateLayoutId)
            else -> onAdShowFail(adUnit, AdError.INVALID_AD_TYPE)
        }
    }

    protected fun getAdResponse(adUnit: AdUnit): AdResponse? = cacheMap.remove(adUnit)

    /** 事件回溯 */
    protected fun onLoadSuccess(adUnit: AdUnit, obj: Any?) {
        cacheMap[adUnit] = AdResponse().apply {
            this.obj = obj
            this.startCacheTime = System.currentTimeMillis()
        }
        loadCallback?.let { it.onLoadSuccess(adUnit) }
    }

    protected fun onLoadFail(adUnit: AdUnit, reason: AdError) =
        loadCallback?.onLoadFail(adUnit, reason)

    protected fun onRequestLoad(adUnit: AdUnit) = loadCallback?.onRequestLoad(adUnit)

    protected fun onAdShow(adUnit: AdUnit) = adCallback?.onAdShow(adUnit)

    protected fun onAdShowFail(adUnit: AdUnit, reason: AdError) =
        adCallback?.onAdShowFail(adUnit, reason)

    protected fun onAdClicked(adUnit: AdUnit) = adCallback?.onAdClicked(adUnit)

    protected fun onAdClosed(adUnit: AdUnit, rewarded: Boolean = false) =
        adCallback?.onAdClosed(adUnit, rewarded)

    protected fun onUserRewarded(adUnit: AdUnit) = adCallback?.onUserRewarded(adUnit)

    protected fun onRequestShow(adUnit: AdUnit) = adCallback?.onRequestShow(adUnit)

    /** 事件回溯 */


    /** 广告平台 */
    abstract suspend fun initPlatform(
        activity: Activity?,
        platform: Platform,
        testMode: Boolean = false
    ): AdError?

    protected abstract fun loadBanner(adUnit: AdUnit)

    protected abstract fun showBanner(adUnit: AdUnit, viewGroup: ViewGroup?)

    protected abstract fun loadInterstitial(adUnit: AdUnit)

    protected abstract fun showInterstitial(adUnit: AdUnit)

    protected abstract fun loadRewardedVideo(adUnit: AdUnit)

    protected abstract fun showRewardedVideo(adUnit: AdUnit)

    protected abstract fun loadAppOpen(adUnit: AdUnit)

    protected abstract fun showAppOpen(adUnit: AdUnit)

    protected abstract fun loadNative(adUnit: AdUnit)

    protected abstract fun showNative(
        adUnit: AdUnit, viewGroup: ViewGroup?, size: Int = 1,
        templateLayoutId: Int = -1
    )

    /** 广告平台 */


    protected fun getActivity(): Activity? = adapterCallback?.getActivity()

}