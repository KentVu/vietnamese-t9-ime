package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.lib.*
import kotlinx.coroutines.channels.Channel
import java.text.Normalizer


internal fun String.composeVietnamese() = Normalizer.normalize(
        this,
        Normalizer.Form.NFKC
)

class DefaultT9Engine(
        private val engineSeed: Sequence<String>,
        override val pad: PadConfiguration,
        lg: LogFactory,
        private val db: Db
) : T9Engine {
    private val log = lg.newLog("T9Engine")

    override val eventSource: Channel<T9Engine.Event> = Channel()

    private val _currentCandidates = mutableSetOf<String>()
    private val _currentNumSeq = mutableListOf<Key>()
    private val findCandidates by lazy {
        FindCandidates(db, pad, 10, log)
    }

    // Preselect 1st candidate
    private var _selectedCandidate = 0

    override suspend fun push(key: Key) {
        _currentNumSeq.add(key)
        when (pad[key].type) {
            KeyType.NextCandidate -> {
                _selectedCandidate++
                eventSource.send(T9Engine.Event.SelectCandidate(_selectedCandidate))
            }
            KeyType.PrevCandidate -> {
                _selectedCandidate--
                eventSource.send(T9Engine.Event.SelectCandidate(_selectedCandidate))
            }
            KeyType.Confirm -> {
                // TODO implement selecting feature
                val selected = if (_currentCandidates.isEmpty()) {
                    // Return the number sequence if no match found.
                    _currentNumSeq
                            .dropLast(1) // Drop the "confirm" key
                            .joinNum()
                } else {
                    _currentCandidates.elementAt(_selectedCandidate)
                }
                eventSource.send(T9Engine.Event.Confirm(selected))
                _currentCandidates.clear()
                _currentNumSeq.clear()
                _selectedCandidate = 0
            }
            else -> {
                // Numeric keys
                val numSeqStr = _currentNumSeq.joinNum()
                log.d("push:$numSeqStr")
                val candidates = findCandidates(_currentNumSeq).toMutableList()
                candidates.add(numSeqStr)
                eventSource.send(T9Engine.Event.NewCandidates(candidates))
                _currentCandidates.clear()
                _currentCandidates.addAll(candidates)
                // XXX: don't use _currentCandidates directly because it is being used on multiple threads
            }
        }
    }

    override suspend fun init() {
        if (db.canReuse()) {
            db.init(engineSeed) { bytes ->
                eventSource.send(T9Engine.Event.LoadProgress(bytes))
            }
        } else {
            db.load()
        }
        eventSource.send(T9Engine.Event.Initialized)
    }

    companion object {
        private fun Iterable<Key>.joinNum(): String = buildString {
            this@joinNum.forEach {
                append(it.char)
            }
        }

        /**
         * Support case-insensitive matching.
         */
        @OptIn(ExperimentalStdlibApi::class)
        private fun Set<Char>.fillCase(): Set<Char> = buildSet {
            this@fillCase.forEach {
                add(it)
                if (Character.isUpperCase(it))
                    add(it.toLowerCase())
                else if (Character.isLowerCase(it))
                    add(it.toUpperCase())
            }
        }
    }

    class FindCandidates(
            private val db: Db,
            private val pad: PadConfiguration,
            private val limit: Int/*TODO*/,
            private val log: LogFactory.Log
    ) {
        operator fun invoke(
                keySeq: List<Key>
        ): Set<String> {
            // find all combinations:
            val possibleCombinations = possibleCombinations("", keySeq)
            return possibleCombinations.fold(sortedSetOf<String>()) { acc, next ->
                acc.apply { addAll(db.search(next).keys.map { it.composeVietnamese() }) }
            }
        }

        private fun possibleCombinations(currentPrefix: String, keySeq: List<Key>): Set<String> {
            val possibleChars = linkedSetOf<Char>()
            pad[keySeq[0]].chars.fillCase().forEach { c ->
                val newCombination = "$currentPrefix$c"
                if (dbContainsPrefix(newCombination))
                    possibleChars.add(c)
            }
            if (keySeq.size == 1) {
                return possibleChars.map {
                    "$currentPrefix$it"
                }.toSet().also {
                    log.v("possibleCombinations:$it")
                }
            }
            // else
            val result = LinkedHashSet<String>()
            possibleChars.forEach { c ->
                result.addAll(possibleCombinations(currentPrefix + c, keySeq.drop(1)))
            }
            return result
        }

        /**
         * Poll db for prefix.
         */
        private fun dbContainsPrefix(combination: String): Boolean {
            val searchResult = db.search(combination).keys
            return searchResult.isNotEmpty()
        }
    }

}

