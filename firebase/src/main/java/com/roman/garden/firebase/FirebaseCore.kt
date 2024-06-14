package com.roman.garden.firebase

import android.app.Application
import android.os.Bundle
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.roman.garden.base.interfaces.IFirebase
import com.roman.garden.base.log.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


internal class FirebaseCore : IFirebase, CoroutineScope by MainScope() {

    private var firebaseAnalytics: FirebaseAnalytics? = null
    private var firebaseRemoteConfig: FirebaseRemoteConfig? = null

    override fun init(application: Application) {
        launch {
            initAnalytics()
            initRemoteConfig()
            initFirebaseMessaging()
        }
    }

    override fun destroy() {
    }

    private fun initAnalytics() {
        firebaseAnalytics = Firebase.analytics
    }

    override fun setUserId(id: String?) {
        firebaseAnalytics?.setUserId(id)
    }

    override fun setUserProperty(key: String, value: String?) {
        firebaseAnalytics?.setUserProperty(key, value)
    }

    override fun setDefaultEventParameters(bundle: Bundle?) {
        firebaseAnalytics?.setDefaultEventParameters(bundle)
    }

    override fun logEvent(eventName: String, bundle: Bundle?) {
        firebaseAnalytics?.logEvent(eventName, bundle)
    }

    private fun initRemoteConfig() {
        firebaseRemoteConfig = Firebase.remoteConfig
        firebaseRemoteConfig?.setConfigSettingsAsync(remoteConfigSettings {
            this.minimumFetchIntervalInSeconds = 60 * 60
        })
    }

    override fun getRemoteConfigBoolean(key: String, defaultValue: Boolean?): Boolean? {
        defaultValue?.let {
            firebaseRemoteConfig?.setDefaultsAsync(mutableMapOf<String, Any>().apply {
                this[key] = defaultValue
            })
        }
        return firebaseRemoteConfig?.getBoolean(key)
    }

    override fun getRemoteConfigString(key: String, defaultValue: String?): String? {
        defaultValue?.let {
            firebaseRemoteConfig?.setDefaultsAsync(mutableMapOf<String, Any>().apply {
                this[key] = defaultValue
            })
        }
        return firebaseRemoteConfig?.getString(key)
    }

    override fun getRemoteConfigDouble(key: String, defaultValue: Double?): Double? {
        defaultValue?.let {
            firebaseRemoteConfig?.setDefaultsAsync(mutableMapOf<String, Any>().apply {
                this[key] = defaultValue
            })
        }
        return firebaseRemoteConfig?.getDouble(key)
    }

    override fun getRemoteConfigLong(key: String, defaultValue: Long?): Long? {
        defaultValue?.let {
            firebaseRemoteConfig?.setDefaultsAsync(mutableMapOf<String, Any>().apply {
                this[key] = defaultValue
            })
        }
        return firebaseRemoteConfig?.getLong(key)
    }

    private fun initFirebaseMessaging() {
        Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Logger.w("Fetching FCM registration token failed:$task.exception")
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            Logger.d("Fetching FCM token: $token")
        })
    }


}