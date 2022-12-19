package com.github.kentvu.t9vietnamese.model

interface App {
    val candidates: Set<String>
    fun init()
    fun type(c: Char)
    fun describe(): String

}
