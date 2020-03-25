package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.lib.*
import kentvu.dawgjava.DawgTrie
import kentvu.dawgjava.Trie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
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
            val candidates = findCandidates(trie, pad, _currentNumSeq, 10).toMutableList()
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

        private fun findCandidates(trie: Trie, pad: PadConfiguration, keySeq: List<Key>, limit: Int/*TODO*/): Set<String> {
            var accumulator = mutableSetOf<String>()
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
            val emptyPrefixes = mutableSetOf<String>()
            return accumulator.fold(sortedSetOf()) { acc, s ->
                acc.apply {
                    // TODO: optimize this: For ex: if 'a' not found then 'aa' 'ab' 'ac' will also not exist!
                    if (emptyPrefixes.contains(s)) return@apply

                    val searchResult = trie.search(s).keys
                    if (searchResult.isEmpty()) emptyPrefixes.add(s)
                    addAll(searchResult.map { it.composeVietnamese() })
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

