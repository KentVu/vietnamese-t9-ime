package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.lib.Key
import com.vutrankien.t9vietnamese.lib.KeyType
import com.vutrankien.t9vietnamese.lib.LogFactory
import com.vutrankien.t9vietnamese.lib.PadConfiguration
import kentvu.dawgjava.DawgTrie
import kentvu.dawgjava.Trie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File

// TODO

class DefaultT9Engine constructor(lg: LogFactory) : T9Engine {
    private val dawgFile = "T9Engine.dawg"
    private val log = lg.newLog("T9Engine")
    private lateinit var trie: Trie
    override var initialized: Boolean = false
        private set
    override lateinit var pad: PadConfiguration

    override val eventSource: Channel<T9Engine.Event> = Channel()

    private val _currentCandidates = mutableSetOf<String>()
    private val _currentNumSeq = mutableListOf<Key>()

    override suspend fun init(
        seed: Sequence<String>
    ) = coroutineScope {
        val channel = Channel<Int>()
        launch(Dispatchers.IO) {
            trie = DawgTrie.build(dawgFile, seed, channel)
        }
        var markPos = 0
        var count = 0
        for (bytes in channel) {
            if (count - markPos == REPORT_PROGRESS_INTERVAL) {
                eventSource.send(T9Engine.Event.LoadProgress(bytes))
                //print("progress $count/${sortedWords.size}: ${bytes.toFloat() / size * 100}%\r")
                markPos = count
            }
        }
        initialized = true
        eventSource.send(T9Engine.Event.Initialized)
    }

    override fun canReuseDb(): Boolean {
        return File(dawgFile).exists()
    }

    override fun initFromDb(dbFile: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun push(key: Key) {
        _currentNumSeq.add(key)
        if (pad[key].type != KeyType.Confirm) {
            val candidates = findCandidates(trie, pad, _currentNumSeq, 10)
            eventSource.send(T9Engine.Event.NewCandidates(candidates))
            _currentCandidates.clear()
            _currentCandidates.addAll(candidates)
            // XXX: don't use _currentCandidates directly because it is being used on multiple threads
        } else {
            // TODO implement selecting feature
            val selected = if (_currentCandidates.isEmpty()) {
                // Return the number sequence if no match found.
                _currentNumSeq
                    .dropLast(1) // Drop the "confirm" key
                    .joinNum()
            } else {
                _currentCandidates.first()
            }
            eventSource.send(T9Engine.Event.Confirm(selected))
            _currentCandidates.clear()
            _currentNumSeq.clear()
        }
    }

    companion object {
        private const val REPORT_PROGRESS_INTERVAL = 10

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

        private fun Iterable<Key>.joinNum(): String = buildString {
            this@joinNum.forEach {
                append(it.char)
            }
        }
    }
}

