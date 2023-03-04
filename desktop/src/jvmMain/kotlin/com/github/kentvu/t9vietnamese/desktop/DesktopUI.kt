package com.github.kentvu.t9vietnamese.desktop

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import com.github.kentvu.t9vietnamese.UIEvent
import com.github.kentvu.t9vietnamese.model.VNKeys
import com.github.kentvu.t9vietnamese.ui.AppUI
import kotlinx.coroutines.flow.*

class DesktopUI(
    override val exitApplication: () -> Unit
) : AppUI() {

    fun injectEvent(ev: UIEvent) {
        eventSource.tryEmit(ev)
    }

}
