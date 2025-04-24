package com.github.kentvu.t9vietnamese.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.t9vietnamese.UI
import com.github.kentvu.t9vietnamese.KeypadEvent
import com.github.kentvu.t9vietnamese.UI.State
import com.github.kentvu.t9vietnamese.model.Action
import com.github.kentvu.t9vietnamese.model.CandidateSelection
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.NumericSubstitution
import com.github.kentvu.t9vietnamese.model.VNKeys
import com.github.kentvu.t9vietnamese.model.VNKeys.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.update
import androidx.compose.ui.input.key.Key as ComposeKey

interface ComposeUI: UI {
    @Composable
    fun AppUi()

    @Composable
    fun ImeUI(state: State, modifier: Modifier = Modifier)
    @Composable
    fun CandidateView(state: State)
}

class T9UI(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val stateSource: MutableStateFlow<State> = MutableStateFlow(State {}),
    private val close: () -> Unit,
) : ComposeUI {

    private val keyEventSource = MutableSharedFlow<KeyEvent>(extraBufferCapacity = 1)

    override fun update(manipulator: State.() -> State) {
        this.stateSource.update { it.manipulator() }
    }

    fun onKeyEvent(keyEvent: KeyEvent): Boolean {
        return keyEventSource.tryEmit(keyEvent)
    }

    /** Translates [KeyEvent] to [Action] */
    private fun handleKeyEvent(
        keyEvent: KeyEvent,
        state: State,
    ) {
        if (keyEvent.isCtrlQ()) {
            state.keypadEventSink(KeypadEvent.CloseRequest)
        } else {
            //onUserEvent(keyEvent, state)
            log.debug("$keyEvent")
            if (keyEvent.type == KeyEventType.KeyUp) {
                if (keyEvent.isCtrlPressed && keyEvent.key == ComposeKey.C) {
                    state.keypadEventSink(KeypadEvent.KeyPress(Action.Clear))
                }
                if (Letter2Keypad.available(keyEvent.key)) {
                    state.keypadEventSink(
                        KeypadEvent.KeyPress(
                            VNKeys.fromChar(
                                Letter2Keypad.numForKey(keyEvent.key)!!
                            ).action
                        )
                    )
                }
            }
        }
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
            ComposeKey.Backspace to '⌫',
        )

        fun available(key: ComposeKey): Boolean {
            return map.containsKey(key)
        }

        fun numForKey(key: ComposeKey): Char? {
            return map[key]
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun AppUi() {
        val state by stateSource.collectAsState()
        //TODO use T9Theme
        MaterialTheme {
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
                    var confirmedText by remember { mutableStateOf("") }
                    TextField(
                        value = confirmedText,
                        modifier = Modifier.semantics { contentDescription=Semantic.testOutput },
                        onValueChange = { confirmedText = it }
                    )
                    val clipboardManager = LocalClipboardManager.current
                    Button({clipboardManager.setText(AnnotatedString(confirmedText))}) {
                        Text("Copy")
                    }
                    CandidateView(state,)
                    ImeUI(state, Modifier.padding(innerPadding))
                }
            }
        }
        if (state.closed) {
            close()
            return
        }
        LaunchedEffect(state) {
            keyEventSource/*.onEach { onUserEvent(it) }.filter { it.isCtrlQ() }*/.collect {
                handleKeyEvent(it, state)
            }
        }
    }

    @Composable
    override fun ImeUI(state: State, modifier: Modifier) {
        Keypad(
            modifier,
            state.initialized
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
    }

    @Composable
    override fun CandidateView(state: State) {
        CandidatesView(state.candidates) {
            state.keypadEventSink(KeypadEvent.CandidateSelect(it))
        }
    }

    @Composable
    fun Keypad(
        modifier: Modifier = Modifier,
        keysEnabled: Boolean,
        onKeyClick: (key: Key, isLong: Boolean) -> Unit,
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
                with(VNKeys) {
                    KeyboardRow(onKeyClick, keysEnabled, Shift, keyOk, keyBackspace)
                    KeyboardRow(onKeyClick, keysEnabled, key1, key2, key3)
                    KeyboardRow(onKeyClick, keysEnabled, key4, key5, key6)
                    KeyboardRow(onKeyClick, keysEnabled, key7, key8, key9)
                    KeyboardRow(onKeyClick, keysEnabled, keyStar, key0, keyHash)
                }
            }
        }
    }

    @Composable
    protected fun CandidatesView(candidates: CandidateSelection, onItemSelected: (Int) -> Unit) {
        val state = rememberLazyListState(candidates.selectedCandidateId)
        LazyRow(
            modifier = Modifier.semantics {
                contentDescription = Semantic.candidates
            }.background(Color.LightGray),
            state = state
        ) {
            candidates.forEachIndexed { i, cand ->
                item(cand.text) {
                    Text(
                        cand.text,
                        Modifier.padding(start = 4.dp)
                            .run {
                                if (candidates.selectedCandidate == cand)
                                    semantics {
                                        contentDescription = Semantic.selectedCandidate
                                    }.background(Color.Gray)
                                else clickable { onItemSelected(i) }
                            }
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
        onKeyClick: (key: Key, isLong: Boolean) -> Unit,
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

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ComposableKey(
        key: Key,
        modifier: Modifier,
        keysEnabled: Boolean,
        onKeyClick: (key: Key, isLong: Boolean) -> Unit,
    ) {
        Button(
            onClick = { onKeyClick(key, false) },
            onLongClick = { onKeyClick(key, true) },
            modifier = modifier,/*.semantics { text = buildAnnotatedString { append(key.symbol) } }*/
            enabled = keysEnabled
        ) {
            Column(
                //Modifier.fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    key.action.symbol,
                    style = MaterialTheme.typography.bodyLarge
                )
                val rawChar = key.action.rawChar
                Text(
                    if (key.longAction != null) {
                        key.longAction!!.symbol
                    } else if (rawChar != null) { /*if (key.action.type == Control)*/
                        if (rawChar.isDigit()) {
                            NumericSubstitution.VN.forNum(rawChar)
                        } else "$rawChar"
                    } else "",
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

    object Semantic {
        const val candidates = "Candidates"
        const val selectedCandidate: String = "selected_candidate"
        const val testOutput: String = "test_output"
    }

    companion object {
        private val log = Logger.tag("AppUI")

        private fun KeyEvent.isCtrlQ(): Boolean {
            return type == KeyEventType.KeyUp && isCtrlPressed && key == ComposeKey.Q
        }

    }
}
