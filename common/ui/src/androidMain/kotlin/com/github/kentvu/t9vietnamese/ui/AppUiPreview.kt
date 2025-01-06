package com.github.kentvu.t9vietnamese.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okio.FileSystem
import okio.Source

@Preview
@Composable
fun AppPreview() {
    object : T9App(object :EnvironmentInteraction {
        override val mainDispatcher: CoroutineDispatcher
            get() = TODO("Not yet implemented")
        override val ioDispatcher: CoroutineDispatcher
            get() = TODO("Not yet implemented")
        override val fileSystem: FileSystem
            get() = TODO("Not yet implemented")
        override val vnWordsSource: Source
            get() = TODO("Not yet implemented")

        override fun finish() {
            TODO("Not yet implemented")
        }
    }) {
        override val ui: AppUI
            = DesktopUI(CoroutineScope(Dispatchers.Default), this)
    }.ui.AppUi()
}
