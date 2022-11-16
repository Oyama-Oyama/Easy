package com.roman.garden.core.ad.config

import android.content.Context
import com.roman.garden.ad.base.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.Charset

internal class Config {

    companion object {
        const val REMOTE_AD_DATA_FILE: String = "remote_ad.json"
    }

    fun readConfig(context: Context){
        var file = checkRemoteAdData(context)
        when(file){
            null -> readAdData(context.assets.open("ad.json"))
            else -> readAdData(FileInputStream(file))
        }
    }

    private fun readAdData(input: InputStream) {
        try {
            input.readBytes().toString(Charset.defaultCharset()).let {
                Logger.d("ad config content:$it")
                JSONObject(it).let { it ->
                    it.optJSONArray("placements").let { it1 ->
                        for(index in 0 until it1.length()){
                            it1.optJSONObject(index).run {
                                if (optString("type") == AdType.BANNER.value) {
                                    optLong("refresh", 30).let { duration ->
                                        AdUnitManager.instance.setBannerRefreshDuration(duration)
                                    }
                                }
                                optJSONArray("adUnits").run {
                                    for (index1 in 0 until length()) {
                                        AdUnit.build(this.optJSONObject(index1)).run {
                                            if (isValid())  AdUnitManager.instance.addAdUnit(this)
                                            else    Logger.w("AdUnit invalid:$this")
                                        }
                                    }
                                }
                            }
                        }
                        AdUnitManager.instance.sortAdUnits()
                    }
                    it.optJSONArray("platforms").let { plat ->
                        for (index in 0 until plat.length()){
                            plat.optJSONObject(index).let { item ->
                                Platform.build(item).let { pt ->
                                    if (pt.isValid())   AdPlatformManager.instance.plusPlatform(pt)
                                    else    Logger.w("platform invalid:$pt")
                                }
                            }
                        }
                    }
                }
            }
        } catch (e : JSONException){
            e.printStackTrace()
        } finally {
            input.close()
        }
    }

    private fun checkRemoteAdData(context: Context) : File? {
       return context.cacheDir.run {
            when(File(this, REMOTE_AD_DATA_FILE).exists()){
                true -> File(this, REMOTE_AD_DATA_FILE).absoluteFile
                false -> null
            }
        }
    }

    private fun cacheRemoteAdData(){

    }

}