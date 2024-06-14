package com.roman.garden.adbase

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

        fun build(json: JSONObject): Platform {
            return Platform().apply {
                title = json.optString("platform")
                appId = json.optString("appKey")
                appSecret = json.optString("appSecret")
            }
        }

    }

}