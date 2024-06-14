package com.roman.garden.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.roman.garden.base.BaseImpl
import com.roman.garden.base.event.AdEventId
import com.roman.garden.base.interfaces.IEvent
import com.roman.garden.base.log.Logger
import com.roman.garden.core.google.*
import com.roman.garden.core.listener.IAdListener
import com.roman.garden.core.proxy.AdEasyProxy
import com.roman.garden.core.proxy.FirebaseProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

open class Easy private constructor() : IEvent, CoroutineScope by MainScope() {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Easy() }
    }

    private var interstitialListener: IAdListener? = null
    private var rewardedListener: IAdListener? = null
    private var appOpenAdListener: IAdListener? = null

    fun init(application: Application, testMode: Boolean = false) {
        BaseImpl.init(application)
        FirebaseProxy.instance.getFirebase()?.init(application)
        /**广告初始化加载*/
        AdEasyProxy.instance.getAdEasy()?.init(application, this, testMode)
    }

    fun destroy(application: Application) {
        interstitialListener = null
        rewardedListener = null
        appOpenAdListener = null
        /**销毁广告*/
        AdEasyProxy.instance.getAdEasy()?.let { it.destroy(application) }
    }

    fun toast(activity: Activity?, str:String){
        activity?.let { ac ->
            Toast.makeText(ac, str, Toast.LENGTH_SHORT).show();
        }
    }

    fun hasBanner(): Boolean = AdEasyProxy.instance.getAdEasy()?.let { it.hasBanner() } == true

    fun showBanner(
        viewGroup: ViewGroup? = null, gravity: Int = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
    ) {
        AdEasyProxy.instance.getAdEasy()?.let { it.showBanner(viewGroup, gravity) }
    }

    fun closeBanner() {
        AdEasyProxy.instance.getAdEasy()?.let { it.closeBanner() }
    }

    fun setInterstitialListener(listener: IAdListener?) {
        interstitialListener = listener
    }

    fun hasInterstitial(): Boolean =
        AdEasyProxy.instance.getAdEasy()?.let { it.hasInterstitial() } == true

    fun showInterstitial() {
        AdEasyProxy.instance.getAdEasy()?.let { it.showInterstitial() }
    }

    fun setRewardedListener(listener: IAdListener?) {
        rewardedListener = listener
    }

    fun hasRewarded(): Boolean = AdEasyProxy.instance.getAdEasy()?.let { it.hasRewarded() } == true

    fun showRewarded() {
        AdEasyProxy.instance.getAdEasy()?.let { it.showRewarded() }
    }

    fun hasNative(): Boolean = AdEasyProxy.instance.getAdEasy()?.let { it.hasNative() } == true

    /**
     * 展示native广告
     * @param
     * viewGroup 广告展示容器
     * size 广告尺寸 @sample {NativeAdUtil.DEFAULT_SMALL_TEMPLATE}  小尺寸
     *                      {NativeAdUtil.DEFAULT_MEDIUM_TEMPLATE} 中等尺寸
     * templaterLayoutId 可提供自定义的布局样式，必须继承自{com.roman.garden.mediation.admob.TemplateView}
     */
    fun showNative(viewGroup: ViewGroup?, size: Int = 1, @LayoutRes templateLayoutId: Int = -1) {
        AdEasyProxy.instance.getAdEasy()?.let { it.showNative(viewGroup, size, templateLayoutId) }
    }

    fun closeNative(viewGroup: ViewGroup?) {
        AdEasyProxy.instance.getAdEasy()?.let { it.closeNative(viewGroup) }
    }

    fun setAppOpenAdListener(listener: IAdListener?) {
        appOpenAdListener = listener
    }

    fun showAppOpenAd() {
        AdEasyProxy.instance.getAdEasy()?.let { it.showAppOpen() }
    }

    /**firebase*/
    fun setUserId(id: String?) {
        FirebaseProxy.instance.getFirebase()?.setUserId(id)
    }

    fun setUserProperty(key: String, value: String?) {
        FirebaseProxy.instance.getFirebase()?.setUserProperty(key, value)
    }

    fun setDefaultEventParameters(bundle: Bundle?) {
        FirebaseProxy.instance.getFirebase()?.setDefaultEventParameters(bundle)
    }

    fun logEvent(eventName: String, bundle: Bundle?) {
        FirebaseProxy.instance.getFirebase()?.logEvent(eventName, bundle)
    }

    fun getRemoteConfigBoolean(key: String, defaultValue: Boolean? = null): Boolean? =
        FirebaseProxy.instance.getFirebase()?.getRemoteConfigBoolean(key, defaultValue)

    fun getRemoteConfigString(key: String, defaultValue: String? = null): String? =
        FirebaseProxy.instance.getFirebase()?.getRemoteConfigString(key, defaultValue)

    fun getRemoteConfigDouble(key: String, defaultValue: Double? = null): Double? =
        FirebaseProxy.instance.getFirebase()?.getRemoteConfigDouble(key, defaultValue)

    fun getRemoteConfigLong(key: String, defaultValue: Long? = null): Long? =
        FirebaseProxy.instance.getFirebase()?.getRemoteConfigLong(key, defaultValue)

    /**end firebase*/

    /**ad event callback*/
    override fun onEvent(eventName: String, bundle: Bundle?) {
        Logger.e(
            "event fired:$eventName, ${bundle?.getString("type")}, ${bundle?.getString("platform")}, ${
                bundle?.getString(
                    "adId"
                )
            }"
        )

        launch {
            when (eventName) {
                AdEventId.EVENT_AD_SHOW -> {
                    getListeners(bundle)?.let { it.onShow() }
                }
                AdEventId.EVENT_AD_CLICKED -> {
                    getListeners(bundle)?.let { it.onClicked() }
                }
                AdEventId.EVENT_AD_SHOW_FAIL -> {
                    val reason = bundle?.let { it.getString("reason") }
                    getListeners(bundle)?.let { it.onShowFail(reason) }
                }
                AdEventId.EVENT_AD_CLOSED -> {
                    val rewarded = bundle?.let { it.getBoolean("rewarded", false) } ?: false
                    getListeners(bundle)?.let {
                        it.onClosed(rewarded)
                    }
                }
                AdEventId.EVENT_AD_REWARDED -> {
                    getListeners(bundle)?.let { it.onUserRewarded() }
                }
            }
            logEvent(eventName, bundle)
        }
    }

    private fun getListeners(bundle: Bundle?): IAdListener? {
        return bundle?.getString("type")?.let {
            return when (it) {
                "interstitial" -> interstitialListener
                "rewarded" -> rewardedListener
                "appOpenAd" -> appOpenAdListener
                else -> null
            }
        }
    }

    /**google 登陆*/

    private var googleSignUtil: GoogleSignUtil? = null
    private fun getGoogleSignUtil(): GoogleSignUtil? {
        if (googleSignUtil == null) googleSignUtil = GoogleSignUtil()
        return googleSignUtil
    }

    /**
     *  静默登陆
     */
    fun signInSilently(activity: Activity, listener: IGoogleSignListener? = null) =
        getGoogleSignUtil()?.apply { this.signInSilently(activity, listener) }
            ?: listener?.onSignInFail(
                Exception("create GoogleSignUtil.kt instance fail")
            )

    /**
     *  正常登陆，需要在Activity内主动调用
     *  @{onActivityResult(requestCode: Int, resultCode: Int, intent: Intent)} 方法
     */
    fun signIn(activity: Activity, listener: IGoogleSignListener? = null) =
        getGoogleSignUtil()?.apply { this.startSignIn(activity, listener) }
            ?: listener?.onSignInFail(
                Exception("create GoogleSignUtil.kt instance fail")
            )

    /**
     *  解析登陆结果
     *  正常登陆时，需要被Activity主动调用
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) =
        getGoogleSignUtil()?.apply { this.onActivityResult(requestCode, resultCode, intent) }

    /**
     * 退出登陆
     */
    fun signOut(activity: Activity, listener: IGoogleSignOutListener? = null) =
        getGoogleSignUtil()?.apply { this.signOut(activity, listener) } ?: listener?.onSignOutFail(
            Exception("create GoogleSignUtil.kt instance fail")
        )

    /**
     * 当前登陆账号
     */
    fun getSignInAccount(context: Context): GoogleSignInAccount? =
        getGoogleSignUtil()?.getSignInAccount(context)

    /**
     * 登陆状态
     */
    fun isSignIn(context: Context): Boolean = getGoogleSignUtil()?.isSignIn(context) ?: false

    /**end google 登陆*/

    /**
     *  榜单
     */

    fun showAllLeaderboards(
        activity: Activity, account: GoogleSignInAccount, listener: IGoogleListener? = null
    ) = PlayServicesUtil.showAllLeaderboards(activity, account, listener)

    /**
     * 显示指定名称排行榜
     */
    fun showLeaderboard(
        activity: Activity,
        account: GoogleSignInAccount,
        displayName: String?,
        listener: IGoogleListener? = null
    ) = PlayServicesUtil.showLeaderboard(activity, account, displayName, listener)

    /**
     * 更新排行榜分数
     */
    fun submitScore(
        context: Context, account: GoogleSignInAccount, displayName: String, score: Long
    ) = PlayServicesUtil.submitScore(context, account, displayName, score)

    fun loadCurrentPlayerLeaderboardScore(
        context: Context,
        account: GoogleSignInAccount,
        displayName: String,
        listener: (rank: String?, score: String?) -> Unit
    ) = PlayServicesUtil.loadCurrentPlayerLeaderboardScore(context, account, displayName, listener)

    fun showAllAchievements(
        activity: Activity, account: GoogleSignInAccount, listener: IGoogleListener? = null
    ) = PlayServicesUtil.showAllAchievements(activity, account, listener)

    fun unlockAchievement(
        context: Context,
        account: GoogleSignInAccount,
        achievement: String,
        immediate: Boolean = false,
        listener: IGoogleListener? = null
    ) = PlayServicesUtil.unlockAchievement(context, account, achievement, immediate, listener)

    fun incrementAchievement(
        context: Context,
        account: GoogleSignInAccount,
        achievement: String,
        progress: Int,
        immediate: Boolean = false,
        listener: IGoogleListener? = null
    ) = PlayServicesUtil.incrementAchievement(context, account, achievement, progress, immediate, listener)

    fun revealAchievement(
        context: Context,
        account: GoogleSignInAccount,
        achievement: String,
        immediate: Boolean = false,
        listener: IGoogleListener? = null
    ) = PlayServicesUtil.revealAchievement(context, account, achievement, immediate, listener)

    fun setStepsAchievement(
        context: Context,
        account: GoogleSignInAccount,
        achievement: String,
        step: Int,
        immediate: Boolean = false,
        listener: IGoogleListener? = null
    ) = PlayServicesUtil.setStepsAchievement(
        context, account, achievement, step, immediate, listener
    )

    /**
     *  榜单  end
     */
}