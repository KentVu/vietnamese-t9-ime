package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.JavaLog
import com.vutrankien.t9vietnamese.Key
import com.vutrankien.t9vietnamese.Logging
import com.vutrankien.t9vietnamese.PadConfiguration
import kentvu.dawgjava.Trie
import kentvu.dawgjava.TrieFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.text.Normalizer

// TODO Have Dagger inject this
private val log: Logging = JavaLog("T9Engine")

class T9EngineFactory {
    companion object {
        fun newEngine(pad: PadConfiguration): T9Engine {
            return DefaultT9Engine(pad)
            //return OldT9Engine(pad)
        }
    }
}

private class DefaultT9Engine(
    override val pad: PadConfiguration
) : T9Engine {
    @Inject
    private lateinit var trie: Trie
    override var initialized: Boolean = false
        private set

    override val eventSource: Channel<T9Engine.Event>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun push(key: Key) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun init(seed: Sequence<String>) {
        val channel = Channel<Int>()
        trie.build(seed, channel)
        //initialized = true
    }
}

private class OldT9Engine(
        override val pad: PadConfiguration
) : T9Engine {
    override var initialized: Boolean = false
    override val eventSource: Channel<T9Engine.Event>
        = Channel(1)
    private val trie: Trie = TrieFactory.newTrie()

    override suspend fun init(seed: Sequence<String>) {
        initialized = true
        //TODO()
        delay(10)
    }

    private val currentNumSeq = mutableListOf<Key>()
    /**
     * * `true`: No more candidates from DB, returning current numseq
     */
    private var numOnlyMode = false
    private var _currentCandidates = setOf<String>()
    var candidates: Set<String>
        get() = if (!numOnlyMode)
            _currentCandidates.map { it.composeVietnamese() }.toSet()
        else setOf(currentNumSeq.map { it.char }.joinToString(separator = ""))
        private set(value) {
            _currentCandidates = value
        }
    private var currentCombinations = setOf<String>()

    override fun push(key: Key) {
        currentNumSeq.push(key)
        if (!numOnlyMode) {
            currentCombinations *= (pad[key].chars)
            // filter combinations
            if (currentNumSeq.size > 1) {
                // Only start from 2 numbers and beyond
                _currentCandidates = currentCombinations.flatMap { trie.search(it).keys }.toSet()
                if (_currentCandidates.isNotEmpty())
                    currentCombinations = currentCombinations.filter { comb ->
                        _currentCandidates.any { it.startsWith(comb) }
                    }.toSet()
                else {
                    log.w("seq$currentNumSeq generates no candidate!!")
                    numOnlyMode = true
                    currentCombinations = setOf()
                }
            }
        } else {
            log.d("NumOnlyMode!")
        }
        log.d("after pushing [$key${pad[key].chars}]: seq${currentNumSeq}comb${currentCombinations}cands$candidates")
    }

    private fun <E> MutableList<E>.push(num: E) = add(num)

    /**
     * Cartesian multiply a Set of Strings with a Set of Chars
     *
     * <br/>i.e. Append each char to each string
     */
    private operator fun Set<String>.times(chars: Set<Char>): Set<String> {
        val newSet = mutableSetOf<String>()
        if (size == 0)
            newSet.addAll(chars)
        else
            chars.forEach { char -> forEach { string -> newSet.add(string + char) } }
        return newSet
    }

    private fun MutableSet<String>.addAll(chars: Set<Char>) {
        chars.forEach { add(it.toString()) }
    }

    private fun String.composeVietnamese() = Normalizer.normalize(this, Normalizer.Form
        .NFKC)

}
