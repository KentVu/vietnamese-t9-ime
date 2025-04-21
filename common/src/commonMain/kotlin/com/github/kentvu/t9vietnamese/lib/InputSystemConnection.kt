package com.github.kentvu.t9vietnamese.lib

/** Wrap the framework IM Service's InputConnection and expose to the domain layer. */
interface InputSystemConnection {

    fun commitText(text: String)
    fun deleteSurroundingText(beforeLength: Int,
                afterLength:Int )
    fun performEditorAction()

}
