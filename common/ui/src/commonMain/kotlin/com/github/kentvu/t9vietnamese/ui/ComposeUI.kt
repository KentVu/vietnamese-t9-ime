package com.github.kentvu.t9vietnamese.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.kentvu.t9vietnamese.Presenter
import com.github.kentvu.t9vietnamese.Presenter.State

interface ComposeUI: Presenter {

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
