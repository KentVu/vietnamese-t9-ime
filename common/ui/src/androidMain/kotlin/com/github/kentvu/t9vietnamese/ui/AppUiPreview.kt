package com.github.kentvu.t9vietnamese.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.kentvu.t9vietnamese.KeypadEvent
import com.github.kentvu.t9vietnamese.T9App
import com.github.kentvu.t9vietnamese.UI
import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import com.github.kentvu.t9vietnamese.model.CandidateSelection
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import okio.FileSystem
import okio.Source

@Preview
@Composable
fun AppPreview() {
    val ui = T9UI(
        CoroutineScope(Dispatchers.Default),
        MutableStateFlow(
            UI.State(
                initialized = true,
                candidates = CandidateSelection.from(listOf("aa", "cc", "dd")),
                confirmedText = "Test UI"
            ) { ev ->
                when (ev) {
                    is KeypadEvent.CandidateSelect -> TODO()
                    KeypadEvent.CloseRequest -> TODO()
                    is KeypadEvent.KeyPress -> TODO()
                }
            }),
        {}
    )
    ui.AppUi()
}
