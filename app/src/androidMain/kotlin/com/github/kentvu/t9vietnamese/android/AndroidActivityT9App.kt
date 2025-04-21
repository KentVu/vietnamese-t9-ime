package com.github.kentvu.t9vietnamese.android

import android.app.Activity
import android.content.Context
import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okio.FileSystem
import okio.Source
import okio.source

class AndroidEnvironmentInteraction(private val context: Context) : EnvironmentInteraction {
    override val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main
    override val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO
    override val fileSystem: FileSystem
        get() = AndroidFileSystem(context)
    override val vnWordsSource: Source
        get() = AndroidEnvironmentInteraction::class.java.classLoader?.getResourceAsStream("vi-DauMoi.dic")!!
            .source()
}
