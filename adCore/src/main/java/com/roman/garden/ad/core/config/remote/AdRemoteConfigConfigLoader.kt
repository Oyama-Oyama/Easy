package com.roman.garden.ad.core.config.remote

import android.content.Context
import com.roman.garden.ad.core.config.ConfigLoader
import com.roman.garden.ad.core.config.IConfigLoader
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

internal class AdRemoteConfigConfigLoader : IConfigLoader {

    override fun getConfigFile(context: Context): InputStream? =
        FileInputStream(File(context.cacheDir, ConfigLoader.AD_CONFIG_FILE))
}