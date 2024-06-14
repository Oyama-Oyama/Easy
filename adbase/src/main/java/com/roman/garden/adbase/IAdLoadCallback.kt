package com.roman.garden.adbase

interface IAdLoadCallback {

    fun onLoadSuccess(adUnit: AdUnit)

    fun onLoadFail(adUnit: AdUnit, reason: AdError)

    fun onRequestLoad(adUnit: AdUnit)

}