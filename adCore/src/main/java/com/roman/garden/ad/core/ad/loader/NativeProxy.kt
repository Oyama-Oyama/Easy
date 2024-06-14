package com.roman.garden.ad.core.ad.loader

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.roman.garden.ad.core.ad.manager.AdConfigManager
import com.roman.garden.adbase.AdType
import com.roman.garden.adbase.AdUnit
import com.roman.garden.base.log.Logger
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

interface NativeAdTask {
    var viewGroup: WeakReference<ViewGroup>
    var size: Int
    var templateLayoutId: Int
    var time: Long

    var showTime: Int
    fun isShowEnoughTime(): Boolean = System.currentTimeMillis() - time >= showTime * 1000L

    fun isValid(): Boolean = viewGroup?.get() != null

}

internal class NativeProxy : BaseProxy {

    constructor() : super() {
        repeatNativeAdTask()
    }

    override fun getType(): AdType = AdType.NATIVE_AD

    override fun isComparePrice(): Boolean = false

    //    private val containers = mutableListOf<WeakReference<ViewGroup>>()
    private val containers = mutableListOf<NativeAdTask>()

    private val nativeAdTaskRunnable: Runnable = Runnable {
        refreshNativeAdTask()
    }

    private fun repeatNativeAdTask() {
        handler.removeCallbacks(nativeAdTaskRunnable)
        handler.postDelayed(
            nativeAdTaskRunnable,
            AdConfigManager.instance.getNativeAdRefreshDuration() * 1000L
        )
    }

    private fun refreshNativeAdTask() {
        val itor = containers.iterator()
        while (itor.hasNext()) {
//            val task = itor.next().let { it.viewGroup.get()?.let { _ -> it } }
            val task = itor.next()
            if (task.isValid()) {
                if (task.isShowEnoughTime()) {
                    this@NativeProxy.showNativeAd(
                        task.viewGroup.get(),
                        task.size,
                        task.templateLayoutId
                    )
                    itor.remove()
                    return
                }
            } else {
                itor.remove()
            }
        }
    }

    override fun showNativeAd(viewGroup: ViewGroup?, size: Int, @LayoutRes templateLayoutId: Int) {
        // TODO: 这里如果同时进入大量展示请求，会发生同步竞争问题
        if (viewGroup == null) return
        launch {
            var isDoShowAction = false
            if (canShow()) {
                isDoShowAction = true
                super.showNativeAd(viewGroup, size, templateLayoutId)
            }
            val task = object : NativeAdTask {
                override var viewGroup: WeakReference<ViewGroup> = WeakReference(viewGroup)
                override var size: Int = size
                override var templateLayoutId: Int = templateLayoutId
                override var showTime: Int = AdConfigManager.instance.getNativeAdRefreshDuration()
                override var time: Long = when (isDoShowAction) {
                    true -> System.currentTimeMillis()
                    false -> -1
                }
            }
            containers.add(task)
            realLoad(null)
        }
    }

    override fun onLoadSuccess(adUnit: AdUnit) {
        // super.onLoadSuccess(adUnit)
        Logger.d("${getType().value} load success and call to show")
        if (containers.size <= 0) return
//        containers.removeLastOrNull()?.let { show(it.get()) } ?: onLoadSuccess(adUnit)
        refreshNativeAdTask()
    }

    override fun closeNative(viewGroup: ViewGroup?) {
        if (viewGroup == null) return
        val itor = containers.iterator()
        while (itor.hasNext()) {
            val task = itor.next()
            if (task.isValid()) {
                if (task.viewGroup.get() == viewGroup) {
                    itor.remove()
                    break
                }
            }
        }
        viewGroup?.removeAllViews()
    }

}