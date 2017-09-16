package com.vutrankien.t9vietnamese;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

/**
 * Created by vutrankien on 17/07/21.
 */

class KLog {
    private final String tag;

    public KLog(String tag) {
        this.tag = tag;
    }

    public void d(String msg) {
        if(BuildConfig.DEBUG) Log.d(tag, msg);
    }

    public void i(@NotNull String msg) {
        Log.i(tag, msg);
    }
}
