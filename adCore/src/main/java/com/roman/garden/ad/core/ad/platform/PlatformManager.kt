package com.roman.garden.ad.core.ad.platform

import android.app.Activity
import com.roman.garden.ad.core.ad.manager.AdConfigManager
import com.roman.garden.adbase.*
import com.roman.garden.base.log.Logger
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class PlatformManager {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { PlatformManager() }
    }

    private val adapters = mutableMapOf<AdPlatform, Adapter>()

    suspend fun initPlatforms(
        activity: Activity?,
        callback: IAdapterCallback?,
        loadCallback: IAdLoadCallback?,
        adCallback: IAdCallback?
    ) {
        withContext(Dispatchers.Default) {
            var time = measureTimeMillis {
                mutableListOf<Job>().let { jobs ->
                    AdConfigManager.instance.getPlatforms().forEach { platform ->
                        jobs.add(
                            launch {
                                AdPlatform.getPlatform(platform.title).let { adPlatform ->
                                    AdapterReflect.getAdapter(adPlatform)?.let {
                                        val err = it.initPlatform(activity, platform)
                                        err?.let {
                                            Logger.e("platform ${adPlatform.value} init error:$it")
                                        } ?: it.apply {
                                            it.setAdapterCallback(callback)
                                            it.setAdCallback(adCallback)
                                            it.setAdLoadCallback(loadCallback)
                                            addPlatform(adPlatform, it)
                                            Logger.d("platform init success:${adPlatform.value}")
                                        }
                                    }
                                }
                            }
                        )
                    }
                    jobs.joinAll()
                }
            }
            Logger.e("all platform init in duration:$time ms")
        }

    }

    fun findPlatform(adUnit: AdUnit?): Adapter? = adUnit?.let { adapters[it.platform] }

    private fun addPlatform(adPlatform: AdPlatform, adapter: Adapter) =
        adapters.also { adapters[adPlatform] = adapter }

    fun destroy() {
        adapters.values.forEach { adapter ->
            adapter.destroy()
        }
        adapters.clear()
    }

}