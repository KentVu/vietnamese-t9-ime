package com.github.kentvu.t9vietnamese.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.t9vietnamese.FakeInputConnection
import com.github.kentvu.t9vietnamese.KeypadEvent
import com.github.kentvu.t9vietnamese.UI
import com.github.kentvu.t9vietnamese.model.Action
import com.github.kentvu.t9vietnamese.model.EditorInfo
import com.github.kentvu.t9vietnamese.ui.theme.T9VietnameseTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class AndroidUI(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val stateSource: MutableStateFlow<UI.State> = MutableStateFlow(UI.State {}),
    private val _ic: FakeInputConnection = FakeInputConnection(),
    private val close: () -> Unit,
    private val launchSystemImSettings: () -> Unit,
    private val launchImePicker: () -> Unit,
) : ComposeUI by ImeServiceUI(scope, stateSource, _ic) {

    private val keyEventSource = MutableSharedFlow<KeyEvent>(extraBufferCapacity = 1)

    fun onKeyEvent(keyEvent: KeyEvent): Boolean {
        return keyEventSource.tryEmit(keyEvent)
    }

    /** Translates [KeyEvent] to [Action] */
    private fun handleKeyEvent(
        keyEvent: KeyEvent,
        state: UI.State,
    ) {
        if (keyEvent.isCtrlQ()) {
            state.keypadEventSink(KeypadEvent.CloseRequest)
        } else {
            //onUserEvent(keyEvent, state)
            log.debug("$keyEvent")
            if (keyEvent.type == KeyEventType.KeyUp) {
                if (keyEvent.isCtrlPressed && keyEvent.key == Key.C) {
                    state.keypadEventSink(KeypadEvent.KeyPress(Action.Clear))
                }
                if (Letter2Keypad.available(keyEvent.key)) {
                    state.keypadEventSink(
                        KeypadEvent.KeyPress(
                            Action.fromChar(
                                Letter2Keypad.numForKey(keyEvent.key)!!
                            )
                        )
                    )
                }
            }
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppUi() {
        val state by stateSource.collectAsState()
        T9VietnameseTheme {
            Scaffold(topBar = {
                TopAppBar(title = {
                    Text("T9Vietnamese")
                })
            }) { innerPadding ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                ) {
                    GuideUserUI(/*Modifier.weight(1f)*/)
                    Row(Modifier.selectableGroup(), Arrangement.spacedBy(16.dp)) {
                        val radioOptions = EditorInfo.Class.entries
                        val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
                        radioOptions.forEach { mode ->
                            Row(
                                Modifier.selectable(
                                    selected = mode == selectedOption,
                                    onClick = { onOptionSelected(mode) },
                                    role = Role.RadioButton
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (mode == selectedOption),
                                    onClick = null // null recommended for accessibility with screen readers
                                )
                                Text(mode.name, Modifier.padding(start = 8.dp))
                            }
                        }
                        LaunchedEffect(selectedOption) {
                            state.keypadEventSink(KeypadEvent.InputViewStart(EditorInfo(selectedOption)))
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f).padding(top = 4.dp)
                    ) {
                        val confirmedText = _ic.confirmedText
                        TextField(
                            value = confirmedText,
                            modifier = Modifier.semantics {
                                contentDescription = ComposeUI.Semantic.testOutput
                            },
                            onValueChange = { _ic.confirmedText = it }
                        )
                        val clipboardManager = LocalClipboardManager.current
                        Button({
                            clipboardManager.setText(
                                AnnotatedString(
                                    confirmedText
                                )
                            )
                        }) {
                            Text("Copy")
                        }
                    }
                    CandidateView(state,)
                    ImeUI(state, Modifier)
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
    private fun /*ColumnScope.*/GuideUserUI(
        modifier: Modifier = Modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxWidth(),
        ) {
            Text("Please enable T9Vietnamese:", Modifier.align(Alignment.Start))
            Button(
                { launchSystemImSettings() },
                Modifier.align(Alignment.Start)
            ) {
                Text("open system im settings")
            }
            Text("You can choose T9Vietnamese as default IM:", Modifier.align(Alignment.Start))
            Button(
                { launchImePicker() },
                Modifier.align(Alignment.Start)
            ) {
                Text("Launch IME picker")
            }
        }
    }

    companion object {
        private val log = Logger.tag("AppUI")

        fun KeyEvent.isCtrlQ(): Boolean {
            return (type == KeyEventType.KeyUp) && isCtrlPressed && (key == Key.Q)
        }
    }
}