package com.vutrankien.t9vietnamese

import android.app.Application

/**
 * Created by vutrankien on 17/08/14.
 */

class T9Application : Application(){
    val engine : T9EngineFactory by lazy {
        T9EngineFactory(this)
    }
}
