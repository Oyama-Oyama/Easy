package com.roman.garden.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.roman.garden.ad.base.ActivityStatus
import com.roman.garden.ad.base.AdStatus
import com.roman.garden.ad.base.AdType
import com.roman.garden.core.ad.AdEasy
import com.roman.garden.core.ad.AdListener
import com.roman.garden.core.ad.EventListener
import com.roman.garden.core.ad.config.Config
import java.lang.ref.WeakReference

open class Easy {

    companion object {
        private val TAG_BANNER_CONTAINER = "_tag_banner_container_"
        private lateinit var _easy: Easy
        open lateinit var context: Context
        private var _activity: WeakReference<Activity>? = null

        open fun init(application: Application) {
            assert(application != null)
            context = application.applicationContext
            _easy = Easy().apply {

                registerApplicationListener(application)
                ActivityStatus.instance.registerActivityListener(application)
                ActivityStatus.instance.activityStateCallback = object : ActivityStatus.IActivityStatusCallback {
                    override fun onActivityState(
                        state: ActivityStatus.ActivityState,
                        activity: Activity
                    ) {
                        when(state){
                            ActivityStatus.ActivityState.CREATED -> {}
                            ActivityStatus.ActivityState.STARTED -> {
                                onStart(activity)
                            }
                            ActivityStatus.ActivityState.RESUMED -> {}
                            ActivityStatus.ActivityState.PAUSED -> {}
                            ActivityStatus.ActivityState.STOPPED -> {
                                hideBanner()
                            }
                            ActivityStatus.ActivityState.DESTROYED -> {
                                hideNativeAd()
                            }
                        }
                    }

                }
                readConfig(application)
            }
        }

        fun onStart(activity: Activity){
            _activity = WeakReference(activity)
        }

        // ====================== ad ==============================================
        fun hasInterstitial(): Boolean {
            return AdEasy.instance.hasInterstitial()
        }

        fun hasRewardedVideo(): Boolean {
            return AdEasy.instance.hasRewardedVideo()
        }

        fun hasBanner(): Boolean {
            return AdEasy.instance.hasBanner()
        }

        fun hasAppOpenAd(): Boolean {
            return AdEasy.instance.hasAppOpenAd()
        }

        fun hasNativeAd(): Boolean {
            return AdEasy.instance.hasNativeAd()
        }

        fun showInterstitial() {
            var result = _activity?.let {
                it.get()?.let { a ->
                    showInterstitial(a)
                    true
                }
            }
            if (result != null && !result!!) {
                AdEasy.instance.notifyAdShowFail(
                    AdType.INTERSTITIAL,
                    AdStatus.AD_SHOW_FAIL.extra("activity can't be null")
                )
            }
        }

        fun showInterstitial(activity: Activity) {
            AdEasy.instance.showInterstitial(activity)
        }

        fun showRewardedVideo() {
            var result = _activity?.let {
                it.get()?.let { a ->
                    showRewardedVideo(a)
                    true
                }
            }
            if (result != null && !result!!) {
                AdEasy.instance.notifyAdShowFail(
                    AdType.REWARDED_VIDEO,
                    AdStatus.AD_SHOW_FAIL.extra("activity can't be null")
                )
            }
        }

        fun showRewardedVideo(activity: Activity) {
            AdEasy.instance.showRewardedVideo(activity)
        }

        fun showBanner() {
            showBanner(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
        }

        fun showBanner(gravity: Int) {
            _activity?.let { it.get()?.let { activity ->
                var decorView = activity.window.decorView as FrameLayout
                decorView.post {
                    var container = FrameLayout(activity)
                    container.tag = TAG_BANNER_CONTAINER
                    var params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, -2)
                    params.gravity = gravity
                    container.layoutParams = params
                    decorView.addView(container)
                    showBanner(container)
                }
            } }
        }

        fun showBanner(container: ViewGroup) {
            AdEasy.instance.showBanner(container)
        }

        fun hideBanner() {
            AdEasy.instance.hideBanner()
            _activity?.let { it.get()?.let { activity ->
                var decorView = activity.window.decorView as FrameLayout
                decorView.post {
                    decorView.findViewWithTag<FrameLayout>(TAG_BANNER_CONTAINER)?.let { pre ->
                        decorView.removeView(pre)
                    }
                }
            } }
        }

        fun showAppOpenAd() {
            var result = _activity?.let {
                it.get()?.let { a ->
                    showAppOpenAd(a)
                    true
                }
            }
            if (result != null && !result!!) {
                AdEasy.instance.notifyAdShowFail(
                    AdType.APP_OPEN_AD,
                    AdStatus.AD_SHOW_FAIL.extra("activity can't be null")
                )
            }
        }

        fun showAppOpenAd(activity: Activity) {
            AdEasy.instance.showAppOpenAd(activity)
        }

        fun showNativeAd(container: ViewGroup) {
            AdEasy.instance.showNativeAd(container)
        }

        fun hideNativeAd() {
            AdEasy.instance.hideNativeAd()
        }

        fun setGlobalAdListener(adListener: AdListener) {
            AdEasy.instance.setGlobalAdListener(adListener)
        }

        fun setGlobalAdEventListener(eventListener: EventListener) {
            //此处需要再拆分，以便接入打点
            AdEasy.instance.setGlobalAdEventListener(eventListener)
        }

        fun setInterstitialListener(adListener: AdListener) {
            AdEasy.instance.setInterstitialListener(adListener)
        }

        fun setRewardedVideoListener(adListener: AdListener) {
            AdEasy.instance.setRewardedVideoListener(adListener)
        }

        fun setAppOpenAdListener(adListener: AdListener) {
            AdEasy.instance.setAppOpenAdListener(adListener)
        }

        fun setBannerListener(adListener: AdListener) {
            AdEasy.instance.setBannerListener(adListener)
        }

        fun setNativeListener(adListener: AdListener) {
            AdEasy.instance.setNativeListener(adListener)
        }
//======================== ad ===========================================


    }

    private var _config: Config? = null

    fun registerApplicationListener(application: Application) {

    }

    fun readConfig(application: Application) {
        // TODO: 读取配置
        _config = Config().apply {
            this.readConfig(application)
            AdEasy.instance.startAdTasks(application)
        }

    }


}