package com.github.kentvu.t9vietnamese.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.kentvu.t9vietnamese.KeypadEvent
import com.github.kentvu.t9vietnamese.UI.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow

class ImeServiceUI(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val stateSource: MutableStateFlow<State> = MutableStateFlow(State {}),
    private val close: () -> Unit,
) : ComposeUI by T9UI(scope, stateSource, close) {

    @Composable
    fun ImeUI() {
        val state by stateSource.collectAsState()
        ImeUI(state)
    }

    @Composable
    fun CandidateView() {
        val state by stateSource.collectAsState()
        CandidateView(state)
    }
}
