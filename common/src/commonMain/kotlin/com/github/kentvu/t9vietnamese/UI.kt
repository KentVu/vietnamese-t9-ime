package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.CandidateSelection
import kotlinx.coroutines.flow.StateFlow

//abstract class UI(private val state: State) {
interface UI {
    fun init(stateSource: StateFlow<State>)

    val stateSource: StateFlow<State>

    data class State(
        val initialized: Boolean = false,
        val closed: Boolean = false,
        //object
        // SelectNextCandidate : UpdateEvent()

        val candidates: CandidateSelection = CandidateSelection(),
        val confirmedText: String = "",
        //https://slackhq.github.io/circuit/states-and-events/
        val keypadEventSink : ((KeypadEvent) -> Unit)
    )
    //class DefaultUI: UI {}
}
