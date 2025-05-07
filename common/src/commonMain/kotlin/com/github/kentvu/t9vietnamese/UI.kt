package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.lib.InputSystemConnection
import com.github.kentvu.t9vietnamese.model.CandidateSelection
import com.github.kentvu.t9vietnamese.model.EditorInfo
import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.KeyPads

//abstract class UI(private val state: State) {
interface UI {

    val inputConnection: InputSystemConnection

    fun updateState(manipulator: (State) -> State)

    data class State(
        val initialized: Boolean = false,
        val closed: Boolean = false,

        val keyPad: KeyPad = KeyPads.VN,
        val candidates: CandidateSelection = CandidateSelection(),
        //https://slackhq.github.io/circuit/states-and-events/
        val keypadEventSink : ((KeypadEvent) -> Unit)
    )
    companion object {
        inline fun UI.update(crossinline manipulator: State.() -> State) =
            updateState { it.manipulator() }
    }
}
