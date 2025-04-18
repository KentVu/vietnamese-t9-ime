package com.github.kentvu.t9vietnamese.model

interface Key {
    val action: Action
    /*@Deprecated("Use NumericSubstitution")
    val subChars: String*/
    /** Long press symbol */
    val longAction: Action?
        //get() = null
}
