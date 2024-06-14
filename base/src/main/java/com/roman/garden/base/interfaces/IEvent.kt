package com.roman.garden.base.interfaces

import android.os.Bundle

interface IEvent {

    fun onEvent(eventName: String, bundle: Bundle?)

}