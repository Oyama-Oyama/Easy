package com.roman.garden.ad.base

import org.json.JSONObject

open class AdUnit {

    var adUnitId: String? = null
    var _platform: String? = null
    var price: Float = 0.0f
    var group: String? = null
    var _adType: String? = null

    var adType: AdType = AdType.INVALID
    var platform: AdPlatform = AdPlatform.INVALID

    open fun isValid(): Boolean {
        with(this) {
            return !adUnitId.isNullOrEmpty() &&
                    price > 0 &&
                    adType.isValid() &&
                    !group.isNullOrEmpty() &&
                    platform.isValid()
        }
    }

    fun isEqualTo(adUnit: AdUnit): Boolean {
        return this.run {
            adUnit.adUnitId == adUnitId &&
                    adUnit.group == group &&
                    adUnit.adType == adType &&
                    adUnit.platform == platform
        }
    }

    override fun toString(): String {
        return "AdUnit(adUnitId=$adUnitId, _platform=$_platform, price=$price, group=$group, _adType=$_adType, adType=$adType, platform=$platform)"
    }


    companion object {

        fun build(
            adUnitId: String?,
            platform: String?,
            price: Float,
            group: String?,
            adType: String?
        ): AdUnit {
            return AdUnit().apply {
                this.adUnitId = adUnitId
                this._platform = platform
                this.price = price
                this.group = group
                this._adType = adType
                this.adType = AdType.getAdType(this._adType)
                this.platform = AdPlatform.getPlatform(this._platform)
            }
        }

        fun build(json: JSONObject): AdUnit {
            return AdUnit().apply {
                this.adUnitId = json.optString("adUnitId", null)
                this._platform = json.optString("platform", null)
                this.price = json.optDouble("price", 0.0).toFloat()
                this.group = json.optString("group", null)
                this._adType = json.optString("adType", null)
                this.adType = AdType.getAdType(this._adType)
                this.platform = AdPlatform.getPlatform(this._platform)
            }
        }

        fun compare(adUnit: AdUnit, adUnit2: AdUnit): Int {
            return ((adUnit.price - adUnit2.price) * 100).toInt()
        }

    }


}