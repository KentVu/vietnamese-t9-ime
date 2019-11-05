package com.vutrankien.t9vietnamese

import android.app.Activity
import android.view.WindowManager

// https://github.com/travis-ci/travis-ci/issues/6340#issuecomment-239537244
internal fun Activity.unlockScreen() {
    val wakeUpDevice = Runnable {
        this.window.addFlags(
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
    }
    this.runOnUiThread(wakeUpDevice)
}
