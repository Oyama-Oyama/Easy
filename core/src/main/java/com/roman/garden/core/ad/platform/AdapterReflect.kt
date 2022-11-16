package com.roman.garden.core.ad.platform

import com.roman.garden.ad.base.AdPlatform
import com.roman.garden.ad.base.Adapter

internal class AdapterReflect {

    companion object {

        fun getAdapter(title: String?): Adapter? {
            try {
                when (title) {
                    AdPlatform.ADMOB.value -> return Class.forName("com.roman.garden.mediation.admob.AdmobAdapter")
                        .newInstance() as Adapter?
                    AdPlatform.UNITY.value -> return Class.forName("com.roman.garden.mediation.unity.UnityAdapter")
                        .newInstance() as Adapter?
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

    }

}