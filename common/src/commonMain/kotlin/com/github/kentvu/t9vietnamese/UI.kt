package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.lib.InputSystemConnection
import com.github.kentvu.t9vietnamese.model.CandidateSelection
import com.github.kentvu.t9vietnamese.model.EditorInfo

//abstract class UI(private val state: State) {
interface UI {

    val inputConnection: InputSystemConnection

    fun update(manipulator: State.() -> State)

    data class State(
        val initialized: Boolean = false,
        val closed: Boolean = false,

        val mode: EditorInfo.Class = EditorInfo.Class.Normal,
        val candidates: CandidateSelection = CandidateSelection(),
        //https://slackhq.github.io/circuit/states-and-events/
        val keypadEventSink : ((KeypadEvent) -> Unit)
    )
    companion object {
        inline fun UI.updateIt(crossinline manipulator: (State) -> State) =
            update { manipulator(this) }
    }
}
