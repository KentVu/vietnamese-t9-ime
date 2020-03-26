package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.lib.*
import kentvu.dawgjava.DawgTrie
import kentvu.dawgjava.Trie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.Normalizer


internal fun String.composeVietnamese() = Normalizer.normalize(
    this,
    Normalizer.Form.NFKC
)

class DefaultT9Engine constructor(
        lg: LogFactory,
        private val env: Env,
        dawgFile: String = "T9Engine.dawg"
) : T9Engine {
    private val dawgPath = "${env.workingDir}/$dawgFile"
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
        log.d("init: fromSeed")
        val channel = Channel<Int>()
        launch(Dispatchers.IO) {
            trie = DawgTrie.build(dawgPath, seed, channel)
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
        // TODO This check always fails on Android!!
        env.fileExists(dawgPath).let {
            log.d("canReuseDb: $it")
            return it
        }
    }

    override fun initFromDb() {
        log.d("initFromDb")
        trie = DawgTrie.load(dawgPath)
    }

    override suspend fun push(key: Key) {
        _currentNumSeq.add(key)
        if (pad[key].type != KeyType.Confirm) {
            log.d("push:${_currentNumSeq.joinNum()}")
            val candidates = findCandidates(trie, pad, _currentNumSeq, 10, log).toMutableList()
            candidates.add(_currentNumSeq.joinNum())
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

        private fun findCandidates(
            trie: Trie,
            pad: PadConfiguration,
            keySeq: List<Key>,
            limit: Int/*TODO*/,
            log: LogFactory.Log
        ): Set<String> {
            var accumulator = sortedSetOf<String>()
            val result = mutableSetOf<String>()
            val emptyPrefixes = mutableSetOf<String>()
            keySeq.forEachIndexed { i, key ->
                if (accumulator.isEmpty()) {
                    accumulator.addAll(pad[key].chars.map { it.toString() })
                } else {
                    // combine chars of current key to current set of combinations
                    val newAcc = sortedSetOf<String>()
                    accumulator.forEach comb@ { comb ->
                        if (emptyPrefixes.contains(comb)) return@comb
                        pad[key].chars.forEach c@ { c ->
                            val newComb = comb + c
                            val searchResult = trie.search(newComb).keys.take(20)
                            log.v("findCandidates:$i:$newComb -> ${searchResult.joinToString()}")
                            if (searchResult.isEmpty()) {
                                emptyPrefixes.add(newComb)
                                return@c // no need to add to the result
                            }
                            newAcc.add(newComb) // add potential combination only
                            if (i == keySeq.lastIndex) {
                                result.addAll(searchResult.map { it.composeVietnamese() })
                            }
                        }
                    }
                    if (newAcc.isEmpty()) {
                        log.d("findCandidates:Empty result @ " +
                                StringBuilder(keySeq/*.take(i + 1)*/.joinNum()).insert(i, '|').toString()
                        )
                        return emptySet() // no candidates found pass this key
                    }
                    accumulator = newAcc
                }
            }
            return result
        }

        private fun Iterable<Key>.joinNum(): String = buildString {
            this@joinNum.forEach {
                append(it.char)
            }
        }
    }
}

