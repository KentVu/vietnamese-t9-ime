package com.github.kentvu.t9vietnamese

class DummyView(t9: T9Engine.Output) : View {
    override val candidates: View.Candidates
        get() = View.Candidates.CandidatesImpl()

}
