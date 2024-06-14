package com.roman.garden.base

import android.app.Application
import android.content.Context
import com.roman.garden.base.util.GpUtil
import com.tencent.mmkv.MMKV
import java.util.*

open class BaseImpl {

    companion object {

        fun init(application: Application) {
            MMKV.initialize(application)
        }

        fun getDefaultCacheInstance(): MMKV = MMKV.defaultMMKV()

        fun getUUID(): String {
            var uuid = getDefaultCacheInstance().decodeString("_base_uuid_", "");
            return uuid?.also { } ?: this.run {
                uuid = UUID.randomUUID().toString()
                getDefaultCacheInstance().encode("_base_uuid_", uuid)
                uuid!!
            }
        }

        fun rate(context: Context, pkgName: String?) {
            pkgName?.let {
                GpUtil().openPlayStore(context, it)
            } ?: GpUtil().openPlayStore(context, context.packageName)
        }

    }


}