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
          ) { key, isLong ->
            if (isLong) {
                if (key.longAction != null)
                    eventSource.tryEmit(
                        KeypadEvent.KeyPress(key.longAction!!))
            } else eventSource.tryEmit(KeypadEvent.KeyPress(key.action))
        }
    }

    @Composable
    fun CandidatesView() {
        CandidatesView(uiState.candidates.value)
    }

}
