package com.roman.garden.core.proxy

import com.roman.garden.base.interfaces.IFirebase
import com.roman.garden.base.log.Logger

internal class FirebaseProxy {


    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { FirebaseProxy() }
    }

    private var firebase: IFirebase? = null

    fun getFirebase(): IFirebase? {
        try {
            firebase?.also {

            } ?: run {
                firebase = Class.forName("com.roman.garden.firebase.FirebaseCore")
                    .newInstance() as IFirebase?
            }
        } catch (e: Exception) {
            Logger.e("创建firebase实例失败:${e.message}")
            e.printStackTrace()
        }
        return firebase
    }


}