package com.github.kentvu.t9vietnamese.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.github.kentvu.t9vietnamese.lib.InputSystemConnection
import com.github.kentvu.t9vietnamese.model.Action
import com.github.kentvu.t9vietnamese.model.CandidateSelection
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.NumericSubstitution
import com.github.kentvu.t9vietnamese.ui.ComposeUI.Semantic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.update

interface ComposeUI: UI {

    @Composable
    fun ImeUI(state: State, modifier: Modifier = Modifier)
    @Composable
    fun CandidateView(state: State)

    object Semantic {
        const val candidates = "Candidates"
        const val selectedCandidate: String = "selected_candidate"
        const val testOutput: String = "test_output"
    }

}
