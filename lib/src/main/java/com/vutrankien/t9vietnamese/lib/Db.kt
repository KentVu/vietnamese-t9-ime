package com.vutrankien.t9vietnamese.lib

interface Db {
    val initialized: Boolean
    fun canReuse(): Boolean
    suspend fun init(seed: Sequence<String>,
                     onBytes: suspend (Int) -> Unit)
    /**
     * Init from built db.
     */
    fun load()
    fun search(prefix: String): Map<String, Int>
}