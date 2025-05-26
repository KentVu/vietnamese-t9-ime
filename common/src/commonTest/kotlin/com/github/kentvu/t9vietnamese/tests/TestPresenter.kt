package com.github.kentvu.t9vietnamese.tests

import com.github.kentvu.t9vietnamese.Presenter
import com.github.kentvu.t9vietnamese.lib.InputSystemConnection

class TestPresenter : Presenter {
    override val inputConnection: InputSystemConnection
        get() = NullInputSystemConnection

    object NullInputSystemConnection : InputSystemConnection {
        override fun commitText(text: String) {
            TODO("Not yet implemented")
        }

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int) {
            TODO("Not yet implemented")
        }

        override fun performEditorAction() {
            TODO("Not yet implemented")
        }

    }

    internal val stateHistory = mutableSetOf(Presenter.State {
        println("TestPresenter:ev:$it")
    })

    override fun updateState(manipulator: (Presenter.State) -> Presenter.State) {
        stateHistory.add(manipulator(stateHistory.last()))
    }

}
