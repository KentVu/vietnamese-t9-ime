package com.vutrankien.t9vietnamese.tests

import kotlinx.coroutines.launch

class Presenter {
    fun attachView(view: View) {
        receiveEvents(view)
    }

    private fun receiveEvents(view: View) {
        view.scope.launch {
            when (view.eventSource.receive()) {
                Event.START -> {
                    view.showProgress()
                }
            }
        }
    }

}
