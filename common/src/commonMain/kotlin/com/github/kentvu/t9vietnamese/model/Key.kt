package com.github.kentvu.t9vietnamese.model

interface Key {
    val action: Action
    /** Long press symbol */
    val longAction: Action?
    private data class AKey(
        override val action: Action,
        override val longAction: Action? = null
    ) : Key {}
    companion object {
        operator fun invoke(
            action: Action,
            longAction: Action? = null
        ): Key {
            return AKey(action, longAction)
        }
    }
}
