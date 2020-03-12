package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.lib.Key
import com.vutrankien.t9vietnamese.lib.KeyType
import com.vutrankien.t9vietnamese.lib.LogFactory
import com.vutrankien.t9vietnamese.lib.PadConfiguration
import kentvu.dawgjava.DawgTrie
import kentvu.dawgjava.Trie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.Normalizer

// TODO

class DefaultT9Engine constructor(lg: LogFactory) : T9Engine {
    private val log = lg.newLog("T9Engine")
    val trie: Trie = DawgTrie()
    override var initialized: Boolean = false
        private set
    override lateinit var pad: PadConfiguration

    override val eventSource: Channel<T9Engine.Event> = Channel()

    private val _currentCandidates = mutableSetOf<String>()
    private val _currentNumSeq = mutableListOf<Key>()

    override suspend fun init(seed: Sequence<String>) {
        val channel = Channel<Int>()
        GlobalScope.launch(Dispatchers.IO) {
            trie.build(seed, channel)
        }
        for (i in channel) {
            eventSource.send(T9Engine.Event.LoadProgress(i))
        }
        initialized = true
        eventSource.send(T9Engine.Event.Initialized)
    }


    override suspend fun push(key: Key) {
        _currentNumSeq.add(key)
        if (pad[key].type != KeyType.Confirm) {
            val candidates = findCandidates(trie, pad, _currentNumSeq, 10)
            eventSource.send(T9Engine.Event.NewCandidates(candidates))
            _currentCandidates.clear()
            _currentCandidates.addAll(candidates)
            // WARNING: _currentCandidates is being used on multiple threads
        } else {
            // TODO implement selecting feature
            eventSource.send(T9Engine.Event.Confirm(_currentCandidates.first()))
            _currentCandidates.clear()
            _currentNumSeq.clear()
        }
    }

    companion object {
        private fun findCandidates(trie: Trie, pad: PadConfiguration, keySeq: List<Key>, limit: Int/*TODO*/): Set<String> {
            var accumulator = mutableSetOf<String>()
            // TODO: use generateSequence!
            keySeq.forEach { key ->
                if (accumulator.isEmpty()) {
                    accumulator.addAll(pad[key].chars.map { it.toString() })
                } else {
                    // combine chars of current key to current set of combinations
                    val newAcc = mutableSetOf<String>()
                    pad[key].chars.forEach { c ->
                        newAcc.addAll(accumulator.map{ it + c })
                    }
                    accumulator = newAcc
                }
            }
            return accumulator.fold(sortedSetOf()) { acc, s ->
                acc.apply {
                    addAll(trie.search(s).keys)
                }
            }
        }
    }
}

//private fun Set<String>.combine(c: Char): Set<String> {
//    if (isEmpty()) {
//        return
//    }
//}

private class OldT9Engine(
    override var pad: PadConfiguration,
    private val log: LogFactory.Log
) : T9Engine {
    override var initialized: Boolean = false

    override val eventSource: Channel<T9Engine.Event>
        = Channel(1)

    private val trie: Trie = DawgTrie()

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
