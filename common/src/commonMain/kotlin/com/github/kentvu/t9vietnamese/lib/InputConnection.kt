package com.github.kentvu.t9vietnamese.lib

interface InputConnection {
    //is UI.UpdateEvent.Confirm -> uiState.apply {
    //    confirmedText.value += event.selectedCandidate.text + " "
    //}

    fun commitText(text: String)
}
