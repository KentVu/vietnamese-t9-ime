package com.github.kentvu.t9vietnamese.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.kentvu.t9vietnamese.UIEvent
import com.github.kentvu.t9vietnamese.ui.AppUI
import com.github.kentvu.t9vietnamese.ui.T9App
import kotlinx.coroutines.CoroutineScope

class ImeServiceUI(scope: CoroutineScope, app: T9App) : AppUI(scope, app) {

    @Composable
    fun ImeUI() {
        Keypad(
            Modifier,
            uiState.initialized.value
        ) { key ->
            eventSource.tryEmit(UIEvent.KeyPress(key))
        }
    }

    @Composable
    fun CandidatesView() {
        CandidatesView(uiState.candidates.value)
    }

}
