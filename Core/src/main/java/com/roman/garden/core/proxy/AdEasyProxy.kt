package com.roman.garden.core.proxy

import com.roman.garden.base.interfaces.IAdCore
import com.roman.garden.base.log.Logger

internal class AdEasyProxy private constructor(){

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AdEasyProxy() }
    }

    private var adEasy: IAdCore? = null

    fun getAdEasy(): IAdCore? {
        try {
            adEasy?.also {

            } ?: run {
                adEasy = Class.forName("com.roman.garden.ad.core.AdEasy").newInstance() as IAdCore?
            }
        } catch (e: Exception) {
            Logger.e("创建广告实例失败:${e.message}")
            e.printStackTrace()
        }
        return adEasy
    }


}