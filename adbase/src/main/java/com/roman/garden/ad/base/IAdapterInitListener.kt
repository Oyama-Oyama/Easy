package com.roman.garden.ad.base

interface IAdapterInitListener {

    fun onInitSuccess()

    fun onInitFail(status: AdStatus)

}