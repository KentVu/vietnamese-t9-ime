package com.vutrankien.t9vietnamese

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree



/**
 * Created by vutrankien on 17/08/14.
 */

class T9Application : Application(){
    val engine : T9EngineFactory by lazy {
        T9EngineFactory(this)
    }

    override fun onCreate() {
        super.onCreate()

//        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
//        } else {
//            Timber.plant(CrashReportingTree())
//        }
    }
}
