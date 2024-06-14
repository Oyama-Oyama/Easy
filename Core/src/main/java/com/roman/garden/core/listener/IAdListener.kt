package com.roman.garden.core.listener

interface IAdListener {

    fun onShow(){}

    fun onClicked(){}

    fun onShowFail(reason: String?){}

    fun onClosed(rewarded:Boolean){}

    fun onUserRewarded(){}

}