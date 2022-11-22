package com.roman.garden.core.ad.proxy

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import com.roman.garden.ad.base.AdStatus
import com.roman.garden.ad.base.AdType
import com.roman.garden.ad.base.AdUnit
import com.roman.garden.ad.base.AdUnitManager
import com.roman.garden.core.ad.AdListener
import com.roman.garden.core.ad.platform.PlatformManager
import java.lang.ref.WeakReference

internal class BannerProxy : AdProxy() {

    private var _container: WeakReference<ViewGroup>? = null
    private var partListener: AdListener? = null
    private var handler: Handler = Handler(Looper.getMainLooper())

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { BannerProxy() }
    }

    private var runnable: Runnable = Runnable {
        run {
            _container?.let {
                it.get()?.let { viewGroup ->
                    show(viewGroup)
                    handler.postDelayed(runnable, AdUnitManager.instance.getBannerRefreshDuration())
                }
            }
        }
    }

    override fun getAdType(): AdType {
        return AdType.BANNER
    }

    override fun getPartListener(): AdListener? {
        return partListener
    }

    override fun setPartListener(adListener: AdListener?) {
        partListener = adListener
    }

    fun show(container: ViewGroup) {
        hide()//先关闭上一个banner，保持全局只存在一个banner循环，何时关闭banner由用户决定
        _container = WeakReference(container)
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, AdUnitManager.instance.getBannerRefreshDuration())
        getCanShowAdUnit().let {
            when (it) {
                null -> onAdShowFail(
                    AdUnit(),
                    AdStatus.AD_SHOW_FAIL.extra("can't find adUnit to Show:${AdType.BANNER}")
                )
                else -> {
                    it.let { adUnit ->
                        PlatformManager.instance.findAdapter(adUnit).let { adapter ->
                            when (adapter) {
                                null -> onAdShowFail(
                                    it,
                                    AdStatus.AD_SHOW_FAIL.extra("can't find adapter to Show ad:${AdType.BANNER}")
                                )
                                else -> adapter.showBanner(adUnit, container)
                            }
                        }
                    }
                }
            }
        }
    }

    fun hide() {
        handler.removeCallbacks(runnable)
        _container?.let {
            it.get()?.let { vg ->
                vg.removeAllViews()
            }
        }
        _container = null
    }


}