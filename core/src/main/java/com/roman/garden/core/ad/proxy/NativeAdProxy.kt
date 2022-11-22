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

internal class NativeAdProxy : AdProxy() {

    private var _container: WeakReference<ViewGroup>? = null
    private var partListener: AdListener? = null
    private var handler: Handler = Handler(Looper.getMainLooper())

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { NativeAdProxy() }
    }

    override fun getAdType(): AdType {
        return AdType.NATIVE_AD
    }

    override fun getPartListener(): AdListener? {
        return partListener
    }

    override fun setPartListener(adListener: AdListener?) {
        partListener = adListener
    }

    private var runnable: Runnable = Runnable {
        run {
            _container?.let {
                it.get()?.let { viewGroup ->
                    show(viewGroup)
                    handler.postDelayed(runnable, 60 * 1000)
                }
            }
        }
    }

    fun show(container: ViewGroup) {
        hide()
        _container = WeakReference(container)
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 60 * 1000)
        getCanShowAdUnit().let {
            when (it) {
                null -> onAdShowFail(
                    AdUnit(),
                    AdStatus.AD_SHOW_FAIL.extra("can't find adUnit to Show:${AdType.NATIVE_AD}")
                )
                else -> {
                    it.let { adUnit ->
                        PlatformManager.instance.findAdapter(adUnit).let { adapter ->
                            when (adapter) {
                                null -> onAdShowFail(
                                    it,
                                    AdStatus.AD_SHOW_FAIL.extra("can't find adapter to Show ad:${AdType.NATIVE_AD}")
                                )
                                else -> adapter.showNative(adUnit, container)
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
                vg.postInvalidate()
            }
        }
        _container = null
    }

}