package com.github.kentvu.t9vietnamese.ui

data class UIState(val initialized: Boolean, val candidates: Set<String>) {

    constructor() : this(false, emptySet())
    constructor(candidates: Set<String>) : this(false, candidates)

}
