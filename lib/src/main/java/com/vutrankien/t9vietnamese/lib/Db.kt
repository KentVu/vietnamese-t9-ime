package com.vutrankien.t9vietnamese.lib

interface Db {
    val initialized: Boolean
    suspend fun initOrLoad(seed: Seed,
                           onBytes: suspend (Int) -> Unit)

    fun search(prefix: String): Map<String, Int>
}