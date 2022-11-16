package com.roman.garden.ad.base

interface IAdLoadListener {

    fun onLoadSuccess(adUnit: AdUnit)

    fun onLoadFail(adUnit: AdUnit, reason: AdStatus)

    fun onRequestLoad(adUnit: AdUnit)

}