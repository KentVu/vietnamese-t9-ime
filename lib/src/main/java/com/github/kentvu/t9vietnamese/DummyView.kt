package com.github.kentvu.t9vietnamese

class DummyView : View {
    override val candidates: View.Candidates
        get() = View.Candidates.CandidatesImpl()

}
