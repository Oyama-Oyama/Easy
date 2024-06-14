package com.roman.garden.base.event

open class AdEventId {

    companion object {
        val EVENT_CONFIG_LOADED: String = "config_event_loaded"
        val EVENT_CONFIG_LOAD_FAIL: String = "config_event_load_fail"
        val EVENT_PROXY_ENPTY: String = "ad_event_proxy_error"
        val EVENT_AD_SHOW: String = "ad_event_show"
        val EVENT_AD_SHOW_FAIL: String = "ad_event_show_fail"
        val EVENT_AD_CLICKED: String = "ad_event_clicked"
        val EVENT_AD_CLOSED: String = "ad_event_closed"
        val EVENT_AD_REWARDED: String = "ad_event_rewarded"
        val EVENT_AD_REQUEST_SHOW: String = "ad_event_request_show"
        val EVENT_AD_REQUEST_LOAD: String = "ad_event_request_load"
        val EVENT_AD_LOAD_SUCCESS: String = "ad_event_load_success"
        val EVENT_AD_LOAD_FAIL: String = "ad_event_load_fail"
    }

}