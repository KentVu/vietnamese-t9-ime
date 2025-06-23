package com.github.kentvu.t9vietnamese.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.t9vietnamese.KeypadEvent
import com.github.kentvu.t9vietnamese.Presenter
import com.github.kentvu.t9vietnamese.Presenter.State
import com.github.kentvu.t9vietnamese.model.Action
import com.github.kentvu.t9vietnamese.model.CandidateSelection
import com.github.kentvu.t9vietnamese.model.EditorInfo
import com.github.kentvu.t9vietnamese.model.Key as ModelKey
import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.ui.CommonUI.Semantic.Companion.attach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

class ImeServiceUI(presenter: Presenter) : CommonUI {
    private val stateSource: StateFlow<State> = presenter.stateSource
    private val keyEventSource = MutableSharedFlow<KeyEvent>(extraBufferCapacity = 1)

    override fun onKeyEvent(keyEvent: KeyEvent): Boolean {
        log.debug("onKeyEvent:$keyEvent")
        return keyEventSource.tryEmit(keyEvent)
    }

    /** Translates [KeyEvent] to [Action] */
    private fun KeyEvent.translate2Domain(): KeypadEvent? {
        return if (isCtrlQ()) {
            KeypadEvent.CloseRequest
        } else {
            if (type == KeyEventType.KeyUp) {
                if (isCtrlPressed && key == Key.C) {
                    KeypadEvent.KeyPress(Action.Clear)
                } else if (Letter2Keypad.available(key)) {
                    KeypadEvent.KeyPress(
                            Action.fromChar(
                                Letter2Keypad.numForKey(key)!!
                            )
                        )
                } else null
            } else null
        }
    }

    object Letter2Keypad {
        @OptIn(ExperimentalComposeUiApi::class)
        private val map = mapOf(
            Key.Zero to '0',
            Key.One to '1',
            Key.Two to '2',
            Key.Three to '3',
            Key.Four to '4',
            Key.Five to '5',
            Key.Six to '6',
            Key.Seven to '7',
            Key.Eight to '8',
            Key.Nine to '9',
            // Next is for simulating a keypad by left-side of the keyboard.
            Key.Spacebar to '0',
            Key.Q to '1',
            Key.W to '2',
            Key.E to '3',
            Key.A to '4',
            Key.S to '5',
            Key.D to '6',
            Key.Z to '7',
            Key.X to '8',
            Key.C to '9',
            Key.Semicolon to '*',
            Key.Backspace to '⌫',
        )

        fun available(key: Key): Boolean {
            return map.containsKey(key)
        }

        fun numForKey(key: Key): Char? {
            return map[key]
        }

    }

    override fun onStartInputView(info: EditorInfo): Boolean {
        //return imServiceEventSource.tryEmit(info)
        stateSource.value.keypadEventSink(KeypadEvent.InputViewStart(info))
        return true
    }

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

    @Composable
    override fun ImeUI(state: State, modifier: Modifier) {
        Keypad(
            modifier,
            state.initialized,
            state.keyPad,
        ) { key, isLong ->
            if (isLong) {
                if (key.longAction != null)
                    state.keypadEventSink(
                        KeypadEvent.KeyPress(key.longAction!!)
                    )
            } else state.keypadEventSink(
                KeypadEvent.KeyPress(key.action)
            )
        }
        LaunchedEffect(state) {
            keyEventSource.collect { ke ->
                ke.translate2Domain()
                    ?.let { state.keypadEventSink(it) }
            }
        }
    }

    @Composable
    fun Keypad(
        modifier: Modifier = Modifier,
        keysEnabled: Boolean,
        keyPad: KeyPad,
        onKeyClick: (key: ModelKey, isLong: Boolean) -> Unit,
    ) {
        //Napier.d("Recompose ${getThreadId()}")
        Surface(
            shape = MaterialTheme.shapes.medium,
            //color = MaterialTheme.colors.secondary,
            modifier = modifier
                .animateContentSize()
                .padding(1.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End,
            ) {
                @Composable
                fun KeyboardRow(vararg keys: ModelKey?) =
                    KeyboardRow(onKeyClick, keysEnabled, *keys)
                with(keyPad) {
                    KeyboardRow(Shift, keyOk, keyBackspace)
                    KeyboardRow(key1, key2, key3)
                    KeyboardRow(key4, key5, key6)
                    KeyboardRow(key7, key8, key9)
                    KeyboardRow(keyStar, key0, keyHash)
                }
            }
        }
    }

    @Composable
    override fun CandidateView(state: State) {
        val modifier = Modifier.background(Color.LightGray)
        if (state.reporting)
            Row(
                modifier.semantics { attach(Semantic.ReportUi) }
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val uri = "https://kentvu.github.io/vietnamese-t9-ime/"
                val handler = LocalUriHandler.current
                Text("Please report at ")
                TextButton(onClick = {
                    handler.openUri(uri)
                    state.keypadEventSink(KeypadEvent.ReportClick)
                }) { Text(uri) }
            }
        else CandidatesView(state.candidates, modifier, onReportClick = {
            state.keypadEventSink(KeypadEvent.ShowReportClick)
        }) {
            state.keypadEventSink(KeypadEvent.CandidateSelect(it))
        }
    }

    @Composable
    protected fun CandidatesView(
        candidates: CandidateSelection,
        modifier: Modifier = Modifier,
        onReportClick: () -> Unit,
        onItemSelected: (Int) -> Unit
    ) {
        val state = rememberLazyListState(candidates.selectedCandidateId)
        LazyRow(
            modifier = modifier
                .semantics { attach(Semantic.Candidates) },
            state = state
        ) {
            candidates.forEachIndexed { i, cand ->
                item(cand.text) {
                    Text(
                        cand.text,
                        Modifier.padding(start = 4.dp)
                            .run {
                                if (candidates.selectedCandidate == cand)
                                    semantics { attach(Semantic.selected_candidate) }
                                        .background(Color.Gray)
                                else clickable { onItemSelected(i) }
                            }
                    )
                }
                if (i == candidates.lastIndex()) item(Semantic.ShowReportUiButton) {
                    Text(
                        "+",
                        Modifier
                            .padding(start = 6.dp)
                            .clickable { onReportClick() }
                            .semantics { attach(Semantic.ShowReportUiButton) }
                    )
                }
            }
        }
        if (state.layoutInfo.visibleItemsInfo.isNotEmpty())
        if ((candidates.selectedCandidateId >= state.layoutInfo.visibleItemsInfo.last().index) ||
            (candidates.selectedCandidateId <= state.layoutInfo.visibleItemsInfo.first().index)) //firstVisibleItemIndex
            LaunchedEffect(candidates) {
                state.scrollToItem(candidates.selectedCandidateId)
            }
    }

    @Composable
    private fun KeyboardRow(
        onKeyClick: (key: ModelKey, isLong: Boolean) -> Unit,
        keysEnabled: Boolean,
        vararg keys: ModelKey?
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

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ComposableKey(
        key: ModelKey?,
        modifier: Modifier,
        keysEnabled: Boolean,
        onKeyClick: (key: ModelKey, isLong: Boolean) -> Unit,
    ) {
        //if (key == null)
        Button(
            onClick = { key?.let { onKeyClick(it, false) } },
            onLongClick = { key?.let { onKeyClick(it, true) } },
            modifier = modifier,/*.semantics { text = buildAnnotatedString { append(key.symbol) } }*/
            enabled = keysEnabled
        ) {
            Column(
                //Modifier.fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    key?.action?.displaySymbol.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    if (key?.longAction != null) {
                        key.longAction!!.displaySymbol
                    } else key?.subChars.orEmpty(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            /*Text(
                "↓",
                Modifier.align(Alignment.CenterStart),
                Color.LightGray
            )*/
        }
    }

    enum class Semantic: CommonUI.Semantic {
        Candidates,
        selected_candidate,
        ShowReportUiButton,
        ReportUi,
        ReportButton,
        ;
        // Example of receiver hell :sigh:
        //val attach: SemanticsPropertyReceiver.() -> Unit = {
        //fun SemanticsPropertyReceiver.attach () {
        //fun attach(receiver: SemanticsPropertyReceiver) = receiver.contentDescription = name
    }

    companion object {
        private val log = Logger.tag("ImeServiceUI")

        fun KeyEvent.isCtrlQ(): Boolean {
            return (type == KeyEventType.KeyUp) && isCtrlPressed && (key == Key.Q)
        }
    }

}
