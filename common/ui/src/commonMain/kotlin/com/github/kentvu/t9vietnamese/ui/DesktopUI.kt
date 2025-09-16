package com.github.kentvu.t9vietnamese.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.t9vietnamese.Presenter.State
import com.github.kentvu.t9vietnamese.model.EditorInfo
import com.github.kentvu.t9vietnamese.ui.CommonUI.Semantic.Companion.attach
import com.github.kentvu.t9vietnamese.ui.theme.T9VietnameseTheme

class DesktopUI(
    presenter: DesktopPresenter,
    private val close: () -> Unit,
) : CommonUI by ImeServiceUI(presenter) {
    internal val stateSource = presenter.stateSource
    private val _ic = presenter._ic

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppUiWrapper(state: State, content: @Composable ColumnScope.() -> Unit) {
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
                    if (state.error != null)
                        Text("Error: ${state.error}", Modifier, Color.Red)
                    content()
                }
            }
        }
        if (state.closed) {
            close()
            return
        }
    }

    @Composable
    fun AppUi() {
        val state by stateSource.collectAsState()
        AppUiWrapper(state) {
            SelectModeUI()
            TestTextField(Modifier.weight(1f))
            CandidateView(state,)
            ImeUI(state, Modifier)
        }
        // Call onStartInputView once to init the keypad (mimicking IM service)
        LaunchedEffect(state.keypadEventSink) {
            onStartInputView(EditorInfo(EditorInfo.Class.Normal))
        }
    }

    @Composable
    internal fun TestTextField(modifier: Modifier = Modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(top = 4.dp)
        ) {
            val confirmedText = _ic.confirmedText
            TextField(
                value = confirmedText,
                //modifier = Modifier.semantics { Semantic.test_output.attach(this) },
                modifier = Modifier.semantics { attach(Semantic.test_output) },
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
    }

    @Composable
    internal fun SelectModeUI() {
        SelectModeUI {
            onStartInputView(EditorInfo(it))
        }
    }

    @Composable
    internal fun SelectModeUI(onModeSelected: (EditorInfo.Class) -> Unit) {
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
                onModeSelected(selectedOption)
            }
        }
    }

    enum class Semantic: CommonUI.Semantic {
        test_output,
    }

    companion object {
        private val log = Logger.tag("DesktopUI")
    }
}
