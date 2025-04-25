package com.github.kentvu.t9vietnamese.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.github.kentvu.t9vietnamese.KeypadEvent
import com.github.kentvu.t9vietnamese.UI.State
import com.github.kentvu.t9vietnamese.lib.InputSystemConnection
import com.github.kentvu.t9vietnamese.model.Action
import com.github.kentvu.t9vietnamese.model.CandidateSelection
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.NumericSubstitution
import com.github.kentvu.t9vietnamese.model.VNKeys
import com.github.kentvu.t9vietnamese.model.VNKeys.*
import com.github.kentvu.t9vietnamese.ui.ComposeUI.Semantic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ImeServiceUI(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val stateSource: MutableStateFlow<State> = MutableStateFlow(State {}),
    override val inputConnection: InputSystemConnection,
) : ComposeUI {

    override fun update(manipulator: State.() -> State) {
        this.stateSource.update { it.manipulator() }
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
    override fun CandidateView(state: State) {
        CandidatesView(state.candidates) {
            state.keypadEventSink(KeypadEvent.CandidateSelect(it))
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
}
