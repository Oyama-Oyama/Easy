package com.roman.garden.ad.base

import android.app.Activity
import android.app.Application
import android.os.Bundle

class ActivityStatus : Application.ActivityLifecycleCallbacks {

    interface IActivityStatusCallback {
        fun onActivityState(state: ActivityState, activity: Activity)
    }

    enum class ActivityState {
        CREATED,
        STARTED,
        RESUMED,
        PAUSED,
        STOPPED,
        DESTROYED;
    }

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ActivityStatus() }
    }

    fun registerActivityListener(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun unregisterActivityListener(application: Application) {
        application.unregisterActivityLifecycleCallbacks(this)
    }

    var activity: Activity? = null

    var activityStateCallback: IActivityStatusCallback? = null

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        activityStateCallback?.onActivityState(ActivityState.CREATED, p0)
    }

    override fun onActivityStarted(p0: Activity) {
        activity = p0
        activityStateCallback?.onActivityState(ActivityState.STARTED, p0)
    }

    override fun onActivityResumed(p0: Activity) {
        activityStateCallback?.onActivityState(ActivityState.RESUMED, p0)
    }

    override fun onActivityPaused(p0: Activity) {
        activityStateCallback?.onActivityState(ActivityState.PAUSED, p0)
    }

    override fun onActivityStopped(p0: Activity) {
        activityStateCallback?.onActivityState(ActivityState.STOPPED, p0)
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {
        activityStateCallback?.onActivityState(ActivityState.DESTROYED, p0)
    }


}