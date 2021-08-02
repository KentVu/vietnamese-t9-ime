package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.lib.Key
import com.vutrankien.t9vietnamese.lib.LogFactory
import com.vutrankien.t9vietnamese.lib.PadConfiguration
import kentvu.dawgjava.Trie
import kotlinx.coroutines.channels.Channel

private class OldT9Engine(
    override val engineSeed: Sequence<String>,
    override var pad: PadConfiguration, private val log: LogFactory.Log
) : T9Engine {
    override val initialized: Boolean
        get() = TODO("Not yet implemented")

    override val eventSource: Channel<T9Engine.Event>
        = Channel(1)

    private lateinit var trie: Trie

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

    override suspend fun push(key: Key) {
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

    override suspend fun init() {
        TODO("Not yet implemented")
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
}