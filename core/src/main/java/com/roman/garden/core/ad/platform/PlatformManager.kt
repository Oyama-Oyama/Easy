package com.roman.garden.core.ad.platform

import android.content.Context
import com.roman.garden.ad.base.*
import com.roman.garden.core.ad.proxy.AdapterListenerProxy

internal class PlatformManager {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { PlatformManager() }
    }

    private var adapters: HashMap<String, Adapter> = hashMapOf()

    fun initAdapters(context: Context) {
        AdPlatformManager.instance.platforms.run {
            for (item in this) {
                item.run {
                    AdapterReflect.getAdapter(title)?.let { it ->
                        it.initAdapter(context, this, object : IAdapterInitListener {
                            override fun onInitFail(status: AdStatus) {
                                Logger.e("ad platform init fail:$this, reason:$status")
                                adapters.remove(item.title)
                            }

                            override fun onInitSuccess() {
                                Logger.e("ad platform init success${item.title}")
                            }
                        })
                        // 无论是否初始化成功，确保已存在adapter对象，保证AdUnit 加载循环正常启动
                        it.setAdListener(AdapterListenerProxy.instance)
                        it.setAdLoadListener(AdapterListenerProxy.instance)
                        adapters[item.title!!] = it
                    }
                }
            }
        }
    }

    fun findAdapter(adUnit: AdUnit?): Adapter? {
        when (adUnit) {
            null -> Logger.d("find adapter error")
            else -> {
                return adapters[adUnit.platform.value]
//                for ((key, value) in adapters){
//                    if (key == adUnit.platform.name)
//                        return value
//                }
            }
        }
        return null
    }

}