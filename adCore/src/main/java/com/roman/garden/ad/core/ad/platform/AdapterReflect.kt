package com.roman.garden.ad.core.ad.platform

import com.roman.garden.adbase.AdPlatform
import com.roman.garden.adbase.Adapter

class AdapterReflect {

    companion object {

        fun getAdapter(platform: AdPlatform): Adapter? {
            try {
                when (platform) {
                    AdPlatform.ADMOB -> return Class.forName("com.roman.garden.mediation.admob.AdmobAdapter")
                        .newInstance() as Adapter?
                    AdPlatform.UNITY -> return Class.forName("com.roman.garden.mediation.unity.UnityAdapter")
                        .newInstance() as Adapter?
                    AdPlatform.VUNGLE -> return Class.forName("com.roman.garden.mediation.vungle.VungleAdapter")
                        .newInstance() as Adapter?
                    AdPlatform.INVALID -> null
                }
            } catch (e: Exception) {
              //  e.printStackTrace()
            }
            return null
        }

    }

}