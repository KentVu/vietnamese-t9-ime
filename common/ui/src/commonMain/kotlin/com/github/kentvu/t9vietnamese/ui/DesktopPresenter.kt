package com.github.kentvu.t9vietnamese.ui

import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.t9vietnamese.FakeInputConnection
import com.github.kentvu.t9vietnamese.KeypadEvent
import com.github.kentvu.t9vietnamese.Presenter
import com.github.kentvu.t9vietnamese.model.Action
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class DesktopPresenter(
    private val scope: CoroutineScope,
    stateSource: MutableStateFlow<Presenter.State> = MutableStateFlow(Presenter.State {}),
    internal val _ic: FakeInputConnection = FakeInputConnection(),
) : Presenter by ImeServicePresenter(scope, stateSource, _ic) {

    companion object {
        private val log = Logger.tag("DesktopPresenter")
    }
}