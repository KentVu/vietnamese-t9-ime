package com.github.kentvu.t9vietnamese.android

import android.app.Activity
import android.content.Context
import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import com.github.kentvu.t9vietnamese.ui.T9App
import com.github.kentvu.t9vietnamese.ui.TestUI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okio.FileSystem
import okio.Source
import okio.source

class AndroidActivityT9App(activity: Activity) :
    T9App(
        ActivityEnvironmentInteraction(activity),
    ) {
    override val ui = TestUI(scope, this)
}

class ActivityEnvironmentInteraction(private val activity: Activity) : AndroidEnvironmentInteraction
    (activity) {
    override fun finish() {
        activity.finish()
    }
}

abstract class AndroidEnvironmentInteraction(private val context: Context) : EnvironmentInteraction {
    override val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main
    override val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO
    override val fileSystem: FileSystem
        get() = AndroidFileSystem(context)
    override val vnWordsSource: Source
        get() = AndroidActivityT9App::class.java.classLoader?.getResourceAsStream("vi-DauMoi.dic")!!
            .source()
}
