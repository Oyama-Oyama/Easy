package com.roman.garden.ad.core.ad.loader

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.roman.garden.ad.core.ad.factory.IAdProxy
import com.roman.garden.ad.core.ad.manager.AdConfigManager
import com.roman.garden.ad.core.ad.platform.PlatformManager
import com.roman.garden.adbase.AdType
import com.roman.garden.adbase.AdUnit

import com.roman.garden.base.log.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map


internal abstract class BaseProxy : IAdProxy, CoroutineScope by MainScope() {

    protected val handler: Handler = Handler(Looper.getMainLooper())
    val REPEAT_LOAD_INTERVAL: Long = 60 * 1000L
    var cancelLoadTask: Boolean = false
    protected var showStartTime: Long = -1L

    override fun startLoad() {
        cancelLoadTask = false
        realLoad(null)
        repeatLoadTask()
    }

    override fun stopLoad() {
        cancelLoadTask = true
        handler.removeCallbacks(runnable)
    }

    override fun destroy() {
        stopLoad()
    }

    override fun onLoadSuccess(adUnit: AdUnit) {

    }

    override fun onLoadFail(adUnit: AdUnit) {
        realLoad(adUnit)
    }

    override fun onClosed() {

    }

    override fun canShow(): Boolean {
        AdConfigManager.instance.getAdUnits(getType())?.forEach { adUnit ->
            PlatformManager.instance.findPlatform(adUnit)?.let { adapter ->
                when (adapter.isValidCache(adUnit)) {
                    true -> return true
                    else -> {}
                }
            }
        }
        return false
    }

    override suspend fun findCanShowAdUnit(): AdUnit? {
        return withContext(Dispatchers.IO) {
            AdConfigManager.instance.getAdUnits(getType())?.forEach { adUnit ->
                PlatformManager.instance.findPlatform(adUnit)?.let { adapter ->
                    when (adapter.isValidCache(adUnit)) {
                        true -> return@withContext adUnit
                        else -> {}
                    }
                }
            }
            null
        }
    }

    override fun show(viewGroup: ViewGroup?) {
        launch {
            findCanShowAdUnit()?.let {
                PlatformManager.instance.findPlatform(it)?.apply {
                    this.show(it, viewGroup)
                    showStartTime = System.currentTimeMillis()
                } ?: Logger.e("show ${getType().value} error: no valid platform")
            } ?: Logger.e("show ${getType().value} error: no valid ad")
        }
    }

    override fun showNativeAd(viewGroup: ViewGroup?, size: Int, templateLayoutId: Int) {
        launch {
            findCanShowAdUnit()?.let {
                PlatformManager.instance.findPlatform(it)?.apply {
                    this.show(it, viewGroup)
                    showStartTime = System.currentTimeMillis()
                } ?: Logger.e("show ${getType().value} error: no valid platform")
            } ?: Logger.e("show ${getType().value} error: no valid ad")
        }
    }

    override fun setListener() {

    }

    override fun resume() {
        //供banner 刷新
    }

    override fun pause() {

    }

    protected fun realLoad(adUnit: AdUnit?) {
        if (cancelLoadTask) return
        launch {
            val highPrice = findCurrentHighPrice()
            for (it in findAdUnits(adUnit)) {
                if (isComparePrice() && it.price < highPrice) {

                } else {
                    PlatformManager.instance.findPlatform(it)?.let { adapter ->
                        adapter.load(it)
                    }
                }
            }
        }
    }

    protected suspend fun getValidCacheCount(): Int {
        var count = 0
        AdConfigManager.instance.getAdUnits(getType())?.asFlow()
            ?.map {
                PlatformManager.instance.findPlatform(it)?.let { adapter ->
                    when (adapter.isValidCache(it)) {
                        true -> true
                        else -> false
                    }
                } ?: false
            }
            ?.flowOn(Dispatchers.IO)
            ?.collect { status ->
                if (status) count++
            }
        Logger.w("current valid ${getType().value} ad cache count:$count")
        return count
    }

    private suspend fun findCurrentHighPrice(): Float {
        return withContext(Dispatchers.IO) {
            AdConfigManager.instance.getAdUnits(getType())?.let { adUnits ->
                adUnits.forEach { adUnit ->
                    PlatformManager.instance.findPlatform(adUnit)?.let { adapter ->
                        when (adapter.isValidCache(adUnit)) {
                            true -> {
                                Logger.w("current ${getType().value} high price is ${adUnit.price}")
                                return@withContext adUnit.price
                            }
                            else -> {}
                        }
                    }
                }
            }
            Logger.w("current ${getType().value} high price is 0.0f")
            0.0f
        }
    }

    protected suspend fun findAdUnits(adUnit: AdUnit?): List<AdUnit> {
        return withContext(Dispatchers.Default) {
            AdConfigManager.instance.findNextAdUnits(getType(), adUnit)
        }
    }

    private fun repeatLoadTask() {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, REPEAT_LOAD_INTERVAL)
    }

    private var runnable = Runnable {
        launch {
            val max = getMaxCacheCount()
            if (max != -1) {
                val count = getValidCacheCount()
                if (count >= getMaxCacheCount()) {
                    return@launch
                }
            }
            realLoad(null)
            repeatLoadTask()
        }
    }

    override fun closeBanner() {

    }

    override fun closeNative(viewGroup: ViewGroup?) {

    }

    private fun getMaxCacheCount(): Int = AdConfigManager.instance.getMaxCachedCount(getType().value)

    protected abstract fun getType(): AdType

    open fun isComparePrice(): Boolean = true


}