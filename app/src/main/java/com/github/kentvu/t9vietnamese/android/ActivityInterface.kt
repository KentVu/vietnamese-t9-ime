package com.github.kentvu.t9vietnamese.android

import android.content.Context
import kotlinx.coroutines.CoroutineScope

interface ActivityInterface {
    val scope: CoroutineScope
    val context: Context

    fun finish()
}
