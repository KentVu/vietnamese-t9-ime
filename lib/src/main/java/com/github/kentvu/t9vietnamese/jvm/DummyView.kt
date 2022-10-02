package com.github.kentvu.t9vietnamese.jvm

import com.github.kentvu.t9vietnamese.T9Engine
import com.github.kentvu.t9vietnamese.model.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DummyView(input: Flow<T9Engine.T9EngineOutput>, scope: CoroutineScope) : View {
    private val _candidates = MutableStateFlow(View.Candidates())
    override val candidates: StateFlow<View.Candidates> =
        _candidates.asStateFlow()
    init {
        scope.launch {
            input.collect {
                _candidates.emit(View.Candidates(it.candidates))
            }
        }
    }
}