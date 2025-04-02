package com.github.kentvu.t9vietnamese.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.kentvu.t9vietnamese.KeypadEvent
import com.github.kentvu.t9vietnamese.lib.InputConnection
import com.github.kentvu.t9vietnamese.ui.AppUI
import com.github.kentvu.t9vietnamese.ui.T9App
import kotlinx.coroutines.CoroutineScope

class ImeServiceUI(
    scope: CoroutineScope,
    app: T9App,
    override val inputConnection: InputConnection
) : AppUI(scope, app) {

    /*override val inputConnection = object*/

    @Composable
    fun ImeUI() {
        Keypad(
            Modifier,
            uiState.initialized.value
        ) { key ->
            eventSource.tryEmit(KeypadEvent.KeyPress(key))
        }
    }

    @Composable
    fun CandidatesView() {
        CandidatesView(uiState.candidates.value)
    }

}
