package com.roman.garden.adbase

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.roman.garden.base.log.Logger
import java.lang.ref.WeakReference

interface ProcessLifecycleListener : Application.ActivityLifecycleCallbacks {

    var currentActivity: WeakReference<Activity>?

    var pkg: String?

//    companion object {
//        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ProcessLifeCycleListener() }
//    }

    fun registerProcessLifecycleListener(application: Application?) {
        pkg = application?.packageName
        application?.registerActivityLifecycleCallbacks(this)
    }

    fun unregisterProcessLifecycleListener(application: Application?) {
        application?.unregisterActivityLifecycleCallbacks(this)
    }

    fun getCurrentActivity(): Activity? {
        return currentActivity?.let { it.get() }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Logger.d("onActivityCreated---${activity.componentName.className}, ${pkg}")
        if (activity.componentName.className.startsWith(pkg!!) ||
            "com.unity3d.player.UnityPlayerActivity" == activity.componentName.className
        ) {
            currentActivity = WeakReference(activity)
            Logger.d("onActivityCreated---current activity got")
        }
    }

    override fun onActivityStarted(activity: Activity) {
        Logger.d("onActivityStarted---${activity.componentName.className}, ${pkg}")
        if (activity.componentName.className.startsWith(pkg!!) ||
            "com.unity3d.player.UnityPlayerActivity" == activity.componentName.className
        ) {
            currentActivity = WeakReference(activity)
            Logger.d("onActivityStarted---current activity got")
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }


}