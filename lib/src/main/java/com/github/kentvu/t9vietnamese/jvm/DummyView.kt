package com.github.kentvu.t9vietnamese.jvm

import com.github.kentvu.t9vietnamese.T9Engine
import com.github.kentvu.t9vietnamese.model.View
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DummyView() : View {
    override val candidates: StateFlow<View.Candidates> =
        MutableStateFlow(View.Candidates.CandidatesImpl())
}