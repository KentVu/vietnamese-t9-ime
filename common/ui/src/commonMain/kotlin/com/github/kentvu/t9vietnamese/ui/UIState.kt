package com.github.kentvu.t9vietnamese.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.github.kentvu.t9vietnamese.model.CandidateSelection

class UIState() {
    //private val _initialized = mutableStateOf(false)
    val initialized: MutableState<Boolean> = mutableStateOf(false)
    //private var _candidates = mutableStateOf(CandidateSet())
    val candidates: MutableState<CandidateSelection> = mutableStateOf(CandidateSelection())
    //private val _selectedCandidate = mutableStateOf(0)
    //val selectedCandidate: MutableState<Int> = mutableStateOf(0)
    val confirmedText = mutableStateOf("")
        //set(value) = field.run { this.value = value }

}
