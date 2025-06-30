package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.lib.InputSystemConnection
import com.github.kentvu.t9vietnamese.model.CandidateSelection
import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.KeyPads
import kotlinx.coroutines.flow.StateFlow

interface Presenter {

    val inputConnection: InputSystemConnection
    val stateSource: StateFlow<State>

    fun updateState(manipulator: (State) -> State)

    data class State(
        val initialized: Boolean = false,
        val error: Exception? = null,
        val closed: Boolean = false,
        val reportInfo: ReportInfo? = null,

        val keyPad: KeyPad = KeyPads.VN,
        val candidates: CandidateSelection = CandidateSelection(),
        //https://slackhq.github.io/circuit/states-and-events/
        val keypadEventSink : ((KeypadEvent) -> Unit)
    )

    data class ReportInfo(
        val numSeq: String,
        val candidates: List<String>
    ) {
        val reportUrl: String = "https://kentvu.github.io/vietnamese-t9-ime/" +
                "report.html?numSeq=$numSeq" +
                "&candidates=${candidates.joinToString(",")}"
    }

    companion object {
        inline fun Presenter.update(crossinline manipulator: State.() -> State) =
            updateState { it.manipulator() }
    }
}
