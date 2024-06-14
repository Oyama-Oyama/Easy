package com.roman.garden.adbase

class AdResponse {

    val AD_RESPONSE_VALID_CACHE_DURATION: Long = 20 * 60 * 1000L
    val AD_RESPONSE_VALID_LOAD_DURATION: Long = 60 * 1000L

    var startLoadTime: Long = -1
    var startCacheTime: Long = -1

    var obj: Any? = null

    fun isValidLoad() : Boolean = (System.currentTimeMillis() - startLoadTime) <= AD_RESPONSE_VALID_LOAD_DURATION

    fun isValidCache(): Boolean = (System.currentTimeMillis() - startCacheTime) <= AD_RESPONSE_VALID_CACHE_DURATION


}