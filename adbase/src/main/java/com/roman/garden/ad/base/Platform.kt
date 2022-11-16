package com.roman.garden.ad.base

import org.json.JSONObject

class Platform {

    var title: String? = null
    var appId: String? = null
    var appSecret: String? = null

    fun isValid(): Boolean {
        return !this.title.isNullOrEmpty() && !this.appId.isNullOrEmpty()
    }

    override fun toString(): String {
        return "Platform(title=$title, appId=$appId, appSecret=$appSecret)"
    }

    companion object {

        open fun build(json: JSONObject): Platform {
            return Platform().apply {
                title = json.optString("platform", null)
                appId = json.optString("appKey", null)
                appSecret = json.optString("appSecret", null)
            }
        }

    }

}