package com.github.kentvu.t9vietnamese.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.github.kentvu.t9vietnamese.Backend
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext

abstract class T9App() {

    protected abstract val ui: AppUI
    protected abstract val backend: Backend


    fun start() {
        Napier.base(DebugAntilog())
        composeClosure {
            LaunchedEffect(1) {
                withContext(Dispatchers.Default) {
                    backend.init()
                }
            }
            ui.AppUi()
        }
    }

    open fun stop() {
        Napier.takeLogarithm()
    }

    abstract fun composeClosure(block: @Composable () -> Unit)
}
