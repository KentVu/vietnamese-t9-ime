package com.github.kentvu.t9vietnamese.model

import kotlinx.coroutines.flow.Flow

interface WordList {
    val name: String

    fun lineSequence(): Flow<String>

}
