package com.github.kentvu.t9vietnamese.ui

import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.t9vietnamese.KeypadEvent
import com.github.kentvu.t9vietnamese.Presenter
import com.github.kentvu.t9vietnamese.Presenter.State
import com.github.kentvu.t9vietnamese.lib.InputSystemConnection
import com.github.kentvu.t9vietnamese.model.CandidateSelection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImeServicePresenter(
    private val scope: CoroutineScope,
    override val stateSource: MutableStateFlow<State> = MutableStateFlow(State {}),
    override val inputConnection: InputSystemConnection,
) : Presenter {

    override fun updateState(manipulator: (State) -> State) {
        this.stateSource.update { manipulator(it) }
    }

    companion object {
        private val log = Logger.tag("ImeServicePresenter")
    }
}
