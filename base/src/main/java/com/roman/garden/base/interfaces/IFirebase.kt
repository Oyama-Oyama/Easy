package com.roman.garden.base.interfaces

import android.app.Application
import android.os.Bundle

interface IFirebase {

    fun init(application: Application)

    fun destroy()

    /**
     *  设置用户id
     */
    fun setUserId(id: String?)

    /**
     * 设置用户属性
     */
    fun setUserProperty(key: String, value: String?)

    /**
     *  设置默认参数
     */
    fun setDefaultEventParameters(bundle: Bundle?)

    /**
     *  事件
     */
    fun logEvent(eventName: String, bundle: Bundle?)

    fun getRemoteConfigBoolean(key: String, defaultValue: Boolean? = null): Boolean?

    fun getRemoteConfigString(key: String, defaultValue: String? = null): String?

    fun getRemoteConfigDouble(key: String, defaultValue: Double? = null): Double?

    fun getRemoteConfigLong(key: String, defaultValue: Long? = null): Long?


}