package com.roman.garden.ad.core.config

import android.content.Context
import com.roman.garden.ad.core.config.local.AdLocalConfigConfigLoader
import com.roman.garden.ad.core.config.remote.AdRemoteConfigConfigLoader
import java.io.File

internal class ConfigLoader {

    fun getAdConfigLoader(context: Context): IConfigLoader? {
        val remoteFile = File(context.cacheDir, AD_CONFIG_FILE)
        return when (remoteFile.exists()) {
            true -> AdRemoteConfigConfigLoader()
            else -> AdLocalConfigConfigLoader()
        }
    }

    companion object {
        const val AD_CONFIG_FILE = "ad.json"
    }


}