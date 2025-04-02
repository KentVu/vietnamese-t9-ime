package com.github.kentvu.t9vietnamese.lib

interface InputConnection {

    fun commitText(text: String)
    fun deleteSurroundingText(beforeLength: Int,
                afterLength:Int )
}
