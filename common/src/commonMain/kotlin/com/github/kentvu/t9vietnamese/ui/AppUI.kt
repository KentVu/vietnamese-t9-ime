package com.github.kentvu.t9vietnamese.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.github.kentvu.t9vietnamese.UI
import com.github.kentvu.t9vietnamese.UIEvent
import com.github.kentvu.t9vietnamese.model.CandidateSelection
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.VNKeys
import com.github.kentvu.t9vietnamese.ui.theme.T9VietnameseTheme
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.compose.ui.input.key.Key as ComposeKey

class AppUI(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val app: T9App,
) : UI {
    protected val eventSource = MutableSharedFlow<UIEvent>(extraBufferCapacity = 1)
    protected val uiState = UIState()

    @Composable
    fun AppUi() {
        //val uiState by uiState.collectAsState()
        T9VietnameseTheme {
            Scaffold(topBar = {
                TopAppBar(title = {
                    Text("T9Vietnamese")
                })
            }) { innerPadding ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.fillMaxSize()
                ) {
                    var confirmedText by uiState.confirmedText
                    TextField(
                        value = confirmedText,
                        modifier = Modifier.semantics { contentDescription=Semantic.testOutput },
                        onValueChange = { confirmedText = it }
                    )
                    CandidatesView(uiState.candidates.value)
                    Keypad(
                        Modifier
                            .padding(innerPadding),
                        uiState.initialized.value
                    ) { key ->
                        eventSource.tryEmit(UIEvent.KeyPress(key))
                    }
                }
            }
        }
    }

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

    @Composable
    fun Keypad(
        modifier: Modifier = Modifier,
        keysEnabled: Boolean,
        onKeyClick: (key: Key) -> Unit
    ) {
        //Napier.d("Recompose ${getThreadId()}")
        Surface(
            shape = MaterialTheme.shapes.medium,
            //color = MaterialTheme.colors.secondary,
            elevation = 1.dp,
            modifier = modifier
                .animateContentSize()
                .padding(1.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End,
            ) {
                with(VNKeys) {
                    KeyboardRow(onKeyClick, keysEnabled, Clear)
                    KeyboardRow(onKeyClick, keysEnabled, key1, key2, key3)
                    KeyboardRow(onKeyClick, keysEnabled, key4, key5, key6)
                    KeyboardRow(onKeyClick, keysEnabled, key7, key8, key9)
                    KeyboardRow(onKeyClick, keysEnabled, keyStar, key0)
                }
            }
        }
    }

    @Composable
    private fun CandidatesView(candidates: CandidateSelection) {
        LazyRow(
            modifier = Modifier.semantics {
                contentDescription = Semantic.candidates
            }) {
            candidates.forEach { cand ->
                item(cand.text) {
                    Text(
                        cand.text,
                        Modifier.padding(start = 4.dp)
                            .run {
                                if (candidates.selectedCandidate == cand)
                                    semantics {
                                        contentDescription = Semantic.selectedCandidate
                                    }.background(Color.LightGray)
                                else this
                            }
                    )
                }
            }
        }
    }

    @Composable
    private fun KeyboardRow(
        onKeyClick: (key: Key) -> Unit,
        keysEnabled: Boolean,
        vararg keys: Key
    ) {
        Row {
            val mod = Modifier
                .padding(1.dp)
                .weight(1F)
            for (key in keys) {
                ComposableKey(key, mod, keysEnabled, onKeyClick)
            }
        }
    }

    @Composable
    private fun ComposableKey(
        key: Key,
        modifier: Modifier,
        keysEnabled: Boolean,
        onKeyClick: (key: Key) -> Unit
    ) {
        Button(
            modifier = modifier/*.semantics { text = buildAnnotatedString { append(key.symbol) } }*/,
            enabled = keysEnabled,
            onClick = { onKeyClick(key) }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${key.symbol}",
                    style = MaterialTheme.typography.subtitle1
                )
                Text(
                    key.subChars,
                    style = MaterialTheme.typography.subtitle2
                )
            }
        }
    }

    object Semantic {
        const val candidates = "Candidates"
        const val selectedCandidate: String = "selected_candidate"
        const val testOutput: String = "test_output"
    }

    override fun subscribeEvents(block: (UIEvent) -> Unit) {
        scope.launch {
            eventSource.collect {
                block(it)
            }
        }
        Napier.d("eventSource.subCount:${eventSource.subscriptionCount.value}")
    }

    override fun update(event: UI.UpdateEvent) {
        when (event) {
            is UI.UpdateEvent.Initialized -> {
                //uiState.update { it.copy(true)  }
                //uiState.value = uiState.value.copy(true)
                uiState.initialized.value = true
            }
            is UI.UpdateEvent.UpdateCandidates -> {
                Napier.d("UpdateCandidates: ${event.candidates}", tag = "DesktopUI")
                //println("UpdateCandidates: ${event.candidates}")
                //uiState.update { it.copy(candidates = event.candidates) }
                uiState.candidates.value = event.candidates
            }
            UI.UpdateEvent.Close -> app.finish()
            //UI.UpdateEvent.SelectNextCandidate -> uiState.advanceSelectedCandidate()
                //uiState.update { it.advanceSelectedCandidate() }
            is UI.UpdateEvent.Confirm -> uiState.apply {
                confirmedText.value += event.selectedCandidate.text + " "
            }
        }
    }

    fun onKeyEvent(keyEvent: KeyEvent): Boolean {
        if (keyEvent.isCtrlQ()) {
            app.finish()
            return true
        }
        return onUserEvent(keyEvent)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    private fun KeyEvent.isCtrlQ(): Boolean {
        return type == KeyEventType.KeyUp && isCtrlPressed && key == ComposeKey.Q
    }

    /**
     * @return event handled.
     */
    @OptIn(ExperimentalComposeUiApi::class)
    private fun onUserEvent(keyEvent: KeyEvent): Boolean {
        Napier.d("$keyEvent")
        if (keyEvent.type == KeyEventType.KeyUp) {
            if (keyEvent.isCtrlPressed && keyEvent.key == ComposeKey.C) {
                eventSource.tryEmit(UIEvent.KeyPress(VNKeys.Clear))
            }
            if (Letter2Keypad.available(keyEvent.key)) {
                eventSource.tryEmit(
                    UIEvent.KeyPress(
                        VNKeys.fromChar(
                            Letter2Keypad.numForKey(keyEvent.key)!!
                        )
                    )
                )
                return true
            }
        }
        // let other handlers receive this event
        return false
    }

    object Letter2Keypad {
        @OptIn(ExperimentalComposeUiApi::class)
        private val map = mapOf(
            ComposeKey.Zero to '0',
            ComposeKey.One to '1',
            ComposeKey.Two to '2',
            ComposeKey.Three to '3',
            ComposeKey.Four to '4',
            ComposeKey.Five to '5',
            ComposeKey.Six to '6',
            ComposeKey.Seven to '7',
            ComposeKey.Eight to '8',
            ComposeKey.Nine to '9',
            // Next is for simulating a keypad by left-side of the keyboard.
            ComposeKey.Spacebar to '0',
            ComposeKey.Q to '1',
            ComposeKey.W to '2',
            ComposeKey.E to '3',
            ComposeKey.A to '4',
            ComposeKey.S to '5',
            ComposeKey.D to '6',
            ComposeKey.Z to '7',
            ComposeKey.X to '8',
            ComposeKey.C to '9',
            ComposeKey.Semicolon to '*',
        )

        fun available(key: ComposeKey): Boolean {
            return map.containsKey(key)
        }

        fun numForKey(key: ComposeKey): Char? {
            return map[key]
        }

    }
}
