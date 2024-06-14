package com.roman.garden.adbase


class AdError constructor(var code: Int = 0, var message: String?) {

    companion object {
        val PLATFORM_INIT_ERROR: AdError = AdError(1, "platform init error")
        val INVALID_AD_TYPE: AdError = AdError(2, "invalid ad type")
        val ADAPTER_INIT_FAIL: AdError = AdError(3, "adapter init failed")
        val AD_LOAD_FAIL: AdError = AdError(3, "load fail")
        val AD_SHOW_FAIL: AdError = AdError(3, "show fail")
    }

    fun zip(detail: String): AdError {
        return AdError(this.code, this.message).apply {
            this.message = this.message?.let {
                "$it:$detail"
            } ?: detail
        }
    }

    override fun toString(): String {
        return "code=$code, message=$message"
    }


}