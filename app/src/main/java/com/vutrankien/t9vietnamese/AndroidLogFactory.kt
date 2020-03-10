package com.vutrankien.t9vietnamese

import android.util.Log
import timber.log.Timber

class AndroidLogFactory: LogFactory {
    override fun newLog(tag: String): LogFactory.Log {
        return object : LogFactory.Log {
            override fun d(msg: String) {
                Log.d(tag, msg)
            }

            override fun i(msg: String) {
                Log.i(tag, msg)
            }

            override fun w(msg: String) {
                Log.w(tag, msg)
            }

        }
    }
}