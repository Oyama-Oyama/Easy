package com.roman.garden.core.google

interface IGoogleListener {

    fun onSuccess()

    fun onFail(e:Exception?)
}