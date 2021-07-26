package com.vutrankien.t9vietnamese.android

import android.app.Application
import com.vutrankien.t9vietnamese.lib.EngineModule

/**
 * Created by vutrankien on 17/08/14.
 */
class T9Application : Application(){
    // TODO: Introduce scoped components.
    val appComponent: ActivityComponent by lazy {
        DaggerActivityComponent.builder()
                .envModule(EnvModule(this))
                .configurationModule(ConfigurationModule(this))
                .engineModule(EngineModule(seed))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
    }
}
