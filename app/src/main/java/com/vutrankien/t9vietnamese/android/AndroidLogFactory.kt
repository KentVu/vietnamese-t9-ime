package com.vutrankien.t9vietnamese.android

import android.util.Log
import com.vutrankien.t9vietnamese.lib.LogFactory

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