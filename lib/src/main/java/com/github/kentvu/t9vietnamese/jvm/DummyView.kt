package com.github.kentvu.t9vietnamese.jvm

import com.github.kentvu.t9vietnamese.T9Engine
import com.github.kentvu.t9vietnamese.model.View

class DummyView() : View {
    override val candidates: View.Candidates
        get() = View.Candidates.CandidatesImpl()

}