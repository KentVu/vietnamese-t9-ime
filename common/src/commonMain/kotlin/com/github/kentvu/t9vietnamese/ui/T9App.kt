package com.github.kentvu.t9vietnamese.ui

import androidx.compose.runtime.Composable
import com.github.kentvu.t9vietnamese.Backend
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class T9App(
    private val scope: CoroutineScope,
    private val ioContext: CoroutineDispatcher = Dispatchers.Default
) {

    protected abstract val ui: AppUI
    protected abstract val backend: Backend


    fun start() {
        Napier.base(DebugAntilog())
        scope.launch(ioContext) {
            backend.init()
        }
        composeClosure {
            ui.AppUi()
        }
    }

    open fun stop() {
        Napier.takeLogarithm()
    }

    abstract fun composeClosure(block: @Composable () -> Unit)
}
