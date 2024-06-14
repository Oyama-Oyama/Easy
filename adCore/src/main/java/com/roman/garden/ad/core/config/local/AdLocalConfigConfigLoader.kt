package com.roman.garden.ad.core.config.local

import android.content.Context
import com.roman.garden.ad.core.config.ConfigLoader
import com.roman.garden.ad.core.config.IConfigLoader
import java.io.InputStream

internal class AdLocalConfigConfigLoader : IConfigLoader {

    override fun getConfigFile(context: Context): InputStream? =
        context.assets.open(ConfigLoader.AD_CONFIG_FILE)

}