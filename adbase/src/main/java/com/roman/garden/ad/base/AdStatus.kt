package com.roman.garden.ad.base

class AdStatus(val code: Int, var message: String) {

    fun extra(message: String) : AdStatus {
        return this.apply {
            var message = this.message + ":" + message
            return AdStatus(this.code, message)
        }
    }

    override fun toString(): String {
        return "AdStatus(code=$code, message='$message')"
    }


    companion object {
        val AD_ADAPTER_INIT_FAIL: AdStatus = AdStatus(0, "adapter init fail")
        val AD_LOAD_SUCCESS: AdStatus = AdStatus(1, "ad load success")
        val AD_LOAD_FAIL: AdStatus = AdStatus(2, "ad load fail")
        val AD_SHOW: AdStatus = AdStatus(3, "ad show")
        val AD_SHOW_FAIL: AdStatus = AdStatus(4, "ad show fail")
        val AD_CLICKED: AdStatus = AdStatus(5, "ad clicked")
        val AD_CLOSE: AdStatus = AdStatus(6, "ad close")
        val AD_REWARDED_USER: AdStatus = AdStatus(7, "ad rewarded user")
        val AD_REQUEST_LOAD: AdStatus = AdStatus(8, "ad request load")
        val AD_REQUEST_SHOW: AdStatus = AdStatus(9, "ad request show")
    }


}