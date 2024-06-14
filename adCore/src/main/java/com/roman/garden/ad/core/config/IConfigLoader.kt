package com.roman.garden.ad.core.config

import android.content.Context
import com.roman.garden.ad.core.ad.manager.AdConfigManager
import com.roman.garden.adbase.AdType
import com.roman.garden.adbase.AdUnit
import com.roman.garden.adbase.Platform
import com.roman.garden.base.log.Logger
import org.json.JSONObject
import java.io.InputStream
import java.nio.charset.Charset

internal interface IConfigLoader {

    fun getConfigFile(context: Context): InputStream?

    fun readConfig(context: Context) {
        getConfigFile(context)?.run {

            this.readBytes().toString(Charset.defaultCharset()).let { source ->
                JSONObject(source).let {
                    it.optJSONArray("placements")?.let { it1 ->
                        for (index in 0 until it1.length()) {
                            it1.optJSONObject(index)?.run {

                                optString("type").let { value ->
                                    when (value) {
                                        AdType.BANNER.value -> {
                                            optInt("refresh", 20).let { duration ->
                                                AdConfigManager.instance.setBannerRefreshDuration(
                                                    duration
                                                )
                                            }
                                        }
                                        AdType.NATIVE_AD.value -> {
                                            optInt("refresh", 20).let { duration ->
                                                AdConfigManager.instance.setNativeAdRefreshDuration(
                                                    duration
                                                )
                                            }
                                        }
                                    }
                                    val max = optInt("max", -1)
                                    AdConfigManager.instance.updateMaxCacheAdMap(value, max)
                                }

                                optJSONArray("adUnits")?.run {
                                    for (index1 in 0 until length()) {
                                        AdUnit.build(this.optJSONObject(index1)).run {
                                            when (isValid()) {
                                                true -> AdConfigManager.instance.addAdUnit(this)
                                                else -> Logger.w("AdUnit invalid:$this")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        AdConfigManager.instance.sortAdUnits()
                    }

                    it.optJSONArray("platforms")?.let { plat ->
                        for (index in 0 until plat.length()) {
                            plat.optJSONObject(index)?.let { item ->
                                Platform.build(item).let { pt ->
                                    when (pt.isValid()) {
                                        true -> AdConfigManager.instance.addPlatform(pt)
                                        else -> Logger.w("platform invalid:$pt")
                                    }
                                }
                            }
                        }
                    }

                }
            }
            this.close()
        }
    }

}