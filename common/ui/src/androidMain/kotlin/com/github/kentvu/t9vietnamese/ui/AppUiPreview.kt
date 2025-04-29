package com.github.kentvu.t9vietnamese.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.github.kentvu.t9vietnamese.KeypadEvent
import com.github.kentvu.t9vietnamese.UI
import com.github.kentvu.t9vietnamese.model.CandidateSelection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow

@Preview
@Composable
fun AppPreview() {
    val ui = remember { AndroidUI(
        CoroutineScope(Dispatchers.Default),
        MutableStateFlow(
            UI.State(
                initialized = true,
                candidates = CandidateSelection.from(listOf("aa", "cc", "dd")),
            ) { ev ->
                when (ev) {
                    is KeypadEvent.CandidateSelect -> TODO()
                    KeypadEvent.CloseRequest -> TODO()
                    is KeypadEvent.KeyPress -> TODO()
                    is KeypadEvent.InputViewStart -> TODO()
                }
              }
        ),
        close = {},
        launchSystemImSettings = {},
        launchImePicker = {},
    ) }
    ui.AppUi()
}
