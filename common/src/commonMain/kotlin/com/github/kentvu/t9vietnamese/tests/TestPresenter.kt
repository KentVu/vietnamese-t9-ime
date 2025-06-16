package com.github.kentvu.t9vietnamese.tests

import com.github.kentvu.t9vietnamese.FakeInputConnection
import com.github.kentvu.t9vietnamese.KeypadEvent
import com.github.kentvu.t9vietnamese.Presenter
import com.github.kentvu.t9vietnamese.lib.InputSystemConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/** It's in commonMain because commonTest cannot be referred from other modules' tests. */
class TestPresenter : Presenter {
    override val inputConnection: InputSystemConnection = FakeInputConnection()
    override val stateSource = MutableStateFlow(Presenter.State {
        println("TestPresenter:ev:$it")
        evHistory.add(it)
    })

    private val stateHistory = mutableSetOf(stateSource.value)
    val evHistory = mutableSetOf<KeypadEvent>()

    override fun updateState(manipulator: (Presenter.State) -> Presenter.State) {
        //manipulator(stateSource.value)
        stateSource.update(manipulator)
        stateHistory.add(stateSource.value)
    }

}
