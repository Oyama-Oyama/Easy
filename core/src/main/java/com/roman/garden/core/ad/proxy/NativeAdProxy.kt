package com.roman.garden.core.ad.proxy

import android.view.ViewGroup
import com.roman.garden.ad.base.AdStatus
import com.roman.garden.ad.base.AdType
import com.roman.garden.ad.base.AdUnit
import com.roman.garden.core.ad.AdListener
import com.roman.garden.core.ad.platform.PlatformManager
import java.lang.ref.WeakReference

internal class NativeAdProxy : AdProxy() {

    private var _container: WeakReference<ViewGroup>? = null
    private var partListener: AdListener? = null

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

    fun show(container: ViewGroup) {
        _container = WeakReference(container)
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
        _container?.let {
            it.get()?.let { vg ->
                vg.removeAllViews()
                vg.postInvalidate()
            }
        }
    }

}