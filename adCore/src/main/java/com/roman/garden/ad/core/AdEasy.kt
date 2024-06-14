package com.roman.garden.ad.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.roman.garden.ad.core.ad.factory.AdProxyFactory
import com.roman.garden.ad.core.ad.factory.IAdProxy
import com.roman.garden.ad.core.ad.manager.AdConfigManager
import com.roman.garden.ad.core.ad.platform.PlatformManager
import com.roman.garden.ad.core.config.ConfigLoader
import com.roman.garden.adbase.*
import com.roman.garden.base.event.AdEventId
import com.roman.garden.base.interfaces.IAdCore
import com.roman.garden.base.interfaces.IEvent
import com.roman.garden.base.log.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

internal class AdEasy : IAdCore, ProcessLifecycleListener, IAdapterCallback, IAdCallback,
    IAdLoadCallback,
    CoroutineScope by MainScope(), LifecycleObserver {

    override var currentActivity: WeakReference<Activity>? = null
    override var pkg: String? = null
    var application: Application? = null

    private var bannerProxy: IAdProxy? = null
    private var interstitialProxy: IAdProxy? = null
    private var rewardedProxy: IAdProxy? = null
    private var appOpenProxy: IAdProxy? = null
    private var nativeProxy: IAdProxy? = null

    private var eventCallback: IEvent? = null

    override fun init(application: Application, eventCallback: IEvent, testMode: Boolean) {
        this.application = application
        this.eventCallback = eventCallback
        registerProcessLifecycleListener(application)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        launch {
            AdConfigManager.instance.testMode = testMode
            application.let {
                ConfigLoader().getAdConfigLoader(it)?.run {
                    readConfig(it.applicationContext)
                } ?: Logger.w("fail to read config: loader error")
            }
            PlatformManager.instance.initPlatforms(
                getCurrentActivity(),
                this@AdEasy,
                this@AdEasy,
                this@AdEasy
            )
            loadAd()
        }
    }

    override fun destroy(application: Application) {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        unregisterProcessLifecycleListener(application)
        PlatformManager.instance.destroy()
        bannerProxy?.destroy()
        interstitialProxy?.destroy()
        rewardedProxy?.destroy()
        appOpenProxy?.destroy()
        nativeProxy?.destroy()
        cancel()
    }

    private fun loadAd() {
        AdConfigManager.instance.getAdConfig(AdType.BANNER)?.let {
            getBannerProxy()?.let { it.startLoad() }
                ?: Bundle().apply {
                    this.putString("type", AdType.BANNER.value)
                    eventCallback?.onEvent(AdEventId.EVENT_PROXY_ENPTY, this)
                    Logger.e("start load banner failed: proxy is null")
                }
        }
        AdConfigManager.instance.getAdConfig(AdType.REWARDED_VIDEO)?.let {
            getRewardedProxy()?.let { it.startLoad() }
                ?: Bundle().apply {
                    this.putString("type", AdType.REWARDED_VIDEO.value)
                    eventCallback?.onEvent(AdEventId.EVENT_PROXY_ENPTY, this)
                    Logger.e("start load rewarded failed: proxy is null")
                }
        }
        AdConfigManager.instance.getAdConfig(AdType.INTERSTITIAL)?.let {
            getInterstitialProxy()?.let { it.startLoad() }
                ?: Bundle().apply {
                    this.putString("type", AdType.INTERSTITIAL.value)
                    eventCallback?.onEvent(AdEventId.EVENT_PROXY_ENPTY, this)
                    Logger.e("start load interstitial failed: proxy is null")
                }
        }
        AdConfigManager.instance.getAdConfig(AdType.APP_OPEN_AD)?.let {
            getAppOpenProxy()?.let { it.startLoad() }
                ?: Bundle().apply {
                    this.putString("type", AdType.APP_OPEN_AD.value)
                    eventCallback?.onEvent(AdEventId.EVENT_PROXY_ENPTY, this)
                    Logger.e("start load appOpen failed: proxy is null")
                }
        }
        AdConfigManager.instance.getAdConfig(AdType.NATIVE_AD)?.let {
            getNativeProxy()?.let { it.startLoad() }
                ?: Bundle().apply {
                    this.putString("type", AdType.NATIVE_AD.value)
                    eventCallback?.onEvent(AdEventId.EVENT_PROXY_ENPTY, this)
                    Logger.e("start load native failed: proxy is null")
                }
        }
    }

    override fun hasBanner(): Boolean = bannerProxy?.canShow() == true

    override fun showBanner(viewGroup: ViewGroup?, gravity: Int) {
        closeBanner()
        getBannerProxy()?.let { proxy ->
            getActivity()?.let { activity ->
                viewGroup?.let {
                    proxy.show(viewGroup)
                } ?: FrameLayout(activity).apply {
                    var params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(-1, -2)
                    params.gravity = gravity
//                    this.layoutParams = params
//                    this.setBackgroundColor(Color.GREEN)
                    (activity.window.decorView as? ViewGroup)?.let {
                        it.addView(this, params)
                        showBanner(this, gravity)
                    }
                }
            } ?: Logger.e("show banner failed: activity is null")
        } ?: Logger.e("show banner failed: proxy is null")
    }

    override fun closeBanner() {
        bannerProxy?.let { it.closeBanner() } ?: Logger.e("close banner failed: proxy is null")
    }

    override fun hasInterstitial(): Boolean = interstitialProxy?.canShow() == true

    override fun showInterstitial() {
        getInterstitialProxy()?.let { it.show() }
            ?: Logger.e("show interstitial failed: proxy is null")
    }

    override fun hasRewarded(): Boolean = rewardedProxy?.canShow() == true

    override fun showRewarded() {
        getRewardedProxy()?.let { it.show() } ?: Logger.e("show rewarded failed: proxy is null")
    }

    override fun hasAppOpenAd(): Boolean = appOpenProxy?.canShow() == true

    override fun showAppOpen() {
        getAppOpenProxy()?.let { it.show() } ?: Logger.e("show AppOpen failed: proxy is null")
    }

    override fun hasNative(): Boolean = nativeProxy?.canShow() == true

    override fun showNative(viewGroup: ViewGroup?, size: Int, templateLayoutId: Int) {
        getNativeProxy()?.let { it.showNativeAd(viewGroup, size, templateLayoutId) }
            ?: Logger.e("show native failed: proxy is null")
    }

    override fun closeNative(viewGroup: ViewGroup?) {
        getNativeProxy()?.let { it.closeNative(viewGroup) }
            ?: Logger.e("close native failed: proxy is null")
    }

    private fun getBannerProxy(): IAdProxy? {
        bannerProxy?.also { } ?: run { bannerProxy = AdProxyFactory.getAdLoader(AdType.BANNER) }
        return bannerProxy
    }

    private fun getInterstitialProxy(): IAdProxy? {
        interstitialProxy?.also { } ?: run {
            interstitialProxy = AdProxyFactory.getAdLoader(AdType.INTERSTITIAL)
        }
        return interstitialProxy
    }

    private fun getRewardedProxy(): IAdProxy? {
        rewardedProxy?.also { } ?: run {
            rewardedProxy = AdProxyFactory.getAdLoader(AdType.REWARDED_VIDEO)
        }
        return rewardedProxy
    }

    private fun getAppOpenProxy(): IAdProxy? {
        appOpenProxy?.also { } ?: run {
            appOpenProxy = AdProxyFactory.getAdLoader(AdType.APP_OPEN_AD)
        }
        return appOpenProxy
    }

    private fun getNativeProxy(): IAdProxy? {
        nativeProxy?.also { } ?: run { nativeProxy = AdProxyFactory.getAdLoader(AdType.NATIVE_AD) }
        return nativeProxy
    }

    override fun getActivity(): Activity? = getCurrentActivity()

    override fun onAdShow(adUnit: AdUnit) {
        buildEventBundle(adUnit).let { eventCallback?.onEvent(AdEventId.EVENT_AD_SHOW, it) }
    }

    override fun onAdShowFail(adUnit: AdUnit, reason: AdError) {
        Logger.e("${adUnit.adType.value} show fail:$reason")
        buildEventBundle(adUnit).let {
            reason.message?.let { msg -> it.putString("reason", msg) }
            eventCallback?.onEvent(AdEventId.EVENT_AD_SHOW_FAIL, it)
        }
    }

    override fun onAdClicked(adUnit: AdUnit) {
        buildEventBundle(adUnit).let { eventCallback?.onEvent(AdEventId.EVENT_AD_CLICKED, it) }
    }

    override fun onAdClosed(adUnit: AdUnit, rewardedUser: Boolean) {
        when (adUnit.adType) {
            AdType.APP_OPEN_AD -> appOpenProxy?.onClosed()
            AdType.INTERSTITIAL -> interstitialProxy?.onClosed()
            AdType.REWARDED_VIDEO -> rewardedProxy?.onClosed()
            AdType.BANNER -> bannerProxy?.onClosed()
            AdType.NATIVE_AD -> nativeProxy?.onClosed()
            else -> {}
        }
        buildEventBundle(adUnit).let {
            it.putBoolean("rewarded", rewardedUser)
            eventCallback?.onEvent(AdEventId.EVENT_AD_CLOSED, it)
        }
    }

    override fun onUserRewarded(adUnit: AdUnit) {
        buildEventBundle(adUnit).let { eventCallback?.onEvent(AdEventId.EVENT_AD_REWARDED, it) }
    }

    override fun onRequestShow(adUnit: AdUnit) {
        buildEventBundle(adUnit).let { eventCallback?.onEvent(AdEventId.EVENT_AD_REQUEST_SHOW, it) }
    }

    override fun onLoadSuccess(adUnit: AdUnit) {
        when (adUnit.adType) {
            AdType.APP_OPEN_AD -> appOpenProxy?.onLoadSuccess(adUnit)
            AdType.INTERSTITIAL -> interstitialProxy?.onLoadSuccess(adUnit)
            AdType.REWARDED_VIDEO -> rewardedProxy?.onLoadSuccess(adUnit)
            AdType.BANNER -> bannerProxy?.onLoadSuccess(adUnit)
            AdType.NATIVE_AD -> nativeProxy?.onLoadSuccess(adUnit)
            else -> {}
        }
        buildEventBundle(adUnit).let { eventCallback?.onEvent(AdEventId.EVENT_AD_LOAD_SUCCESS, it) }
    }

    override fun onLoadFail(adUnit: AdUnit, reason: AdError) {
        Logger.e("${adUnit.adType.value} load fail:$reason")
        when (adUnit.adType) {
            AdType.APP_OPEN_AD -> appOpenProxy?.onLoadFail(adUnit)
            AdType.INTERSTITIAL -> interstitialProxy?.onLoadFail(adUnit)
            AdType.REWARDED_VIDEO -> rewardedProxy?.onLoadFail(adUnit)
            AdType.BANNER -> bannerProxy?.onLoadFail(adUnit)
            AdType.NATIVE_AD -> nativeProxy?.onLoadFail(adUnit)
            else -> {}
        }
        buildEventBundle(adUnit).let {
            reason.message?.let { msg -> it.putString("reason", msg) }
            eventCallback?.onEvent(AdEventId.EVENT_AD_LOAD_FAIL, it)
        }
    }

    override fun onRequestLoad(adUnit: AdUnit) {
        buildEventBundle(adUnit).let { eventCallback?.onEvent(AdEventId.EVENT_AD_REQUEST_LOAD, it) }
    }

    private fun buildEventBundle(adUnit: AdUnit): Bundle {
        return Bundle().apply {
            this.putString("adId", adUnit.adUnitId)
            this.putString("type", adUnit.adType.value)
            this.putString("platform", adUnit.platform.value)
            this.putFloat("price", adUnit.price)
        }
    }

}