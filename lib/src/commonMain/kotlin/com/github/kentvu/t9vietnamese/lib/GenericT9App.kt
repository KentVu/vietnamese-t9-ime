package com.github.kentvu.t9vietnamese.lib

import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.T9AppEvent
import com.github.kentvu.t9vietnamese.model.WordList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.FileSystem

class GenericT9App(
    override val keyPad: KeyPad,
    wordlist: WordList,
    fileSystem: FileSystem
): T9App {

    private val engine = Engine(wordlist, fileSystem)

    override fun type(key: Key): Flow<T9AppEvent> = flow {
        engine.type(key)
        //eventFlow.tryEmit(T9AppEvent.UpdateCandidates(engine.candidates)).takeIf { !it }
        //    ?: Napier.e("Try emits failed! $key")
        emit(T9AppEvent.UpdateCandidates(engine.candidates))
    }

    override fun describe(): String {
        return "KeyPad: " + keyPad.describe()
    }

    override fun init() {
        engine.init()
    }

}