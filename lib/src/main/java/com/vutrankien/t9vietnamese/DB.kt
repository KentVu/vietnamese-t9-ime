package com.vutrankien.t9vietnamese

import java.io.Closeable

interface DB : Closeable {
    fun clear()
    fun put(key: String, value: Int)
    fun get(key: String): Int?
    override fun close()
    fun haveMagic(): Boolean
    fun putAll(keys: List<String>, defaultValue: Int = 0)
    fun existingPrefix(prefixes: Set<String>): Set<String>
    fun putMagic()
}