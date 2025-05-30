package com.github.kentvu.t9vietnamese.model

data class Key(
    val action: Action,
    val subChars: String? = null,
    val longAction: Action? = null
) {}

