package com.github.kentvu.t9vietnamese.lib

import com.github.kentvu.t9vietnamese.UI
import com.github.kentvu.t9vietnamese.model.*
import com.github.kentvu.t9vietnamese.model.VNKeys
import kotlin.apply
import kotlin.collections.toMutableList
import kotlin.collections.toSet
import kotlin.text.deleteAt
import kotlin.text.dropLast
import kotlin.text.isNotEmpty
import kotlin.text.last
import kotlin.text.lastIndex

class Engine(private val ui: UI, private val trie: Trie) {
    var candidates: CandidateSelection = CandidateSelection()
        private set
    private val fullSequence = StringBuilder(10)
    private var prefixes: Set<String> = emptySet()
    private val prefixesCache= mutableMapOf<String, Set<String>>()

    fun type(keySequence: String) {
        keySequence.forEach { k ->
            type(VNKeys.fromChar(k))
        }
    }
    fun type(keySequence: List<Key>) {
        keySequence.forEach { key ->
            //if(key.isWordTerminal)
            type(key)
        }
    }

    fun type(key: Key) {
        if (key == VNKeys.Clear) {
            reset()
            ui.update(UI.UpdateEvent.UpdateCandidates(candidates))
            return
        }
        if (key == VNKeys.keyOk) {
            if (fullSequence.isNotEmpty()) {
                ui.inputConnection.commitText(candidates.selectedCandidate.text)
                reset()
                ui.update(UI.UpdateEvent.UpdateCandidates(candidates))
            } else {
                ui.inputConnection.performEditorAction()
            }
            return
        }
        if (key == VNKeys.keyStar) {
            //ui.update(UI.UpdateEvent.SelectNextCandidate)
            candidates = candidates.advanceSelectedCandidate()
            ui.update(UI.UpdateEvent.UpdateCandidates(candidates))
            return
        }
        if (key == VNKeys.keyHash) {
            // Commit fullSequence directly.
            /*ui.inputConnection.commitText(fullSequence.toString())
            reset()
            ui.update(UI.UpdateEvent.UpdateCandidates(candidates))*/
            // Hash button: select the number sequence.
            candidates = candidates.select(candidates.lastIndex())
            ui.update(UI.UpdateEvent.UpdateCandidates(candidates))
            return
        }
        if (key == VNKeys.key0) {
            if (candidates.isNotEmpty()) {
                ui.inputConnection.commitText(candidates.selectedCandidate.text)
            } else {
                ui.inputConnection.commitText("${VNKeys.key0.subChars.first()}")
            }
            reset()
            ui.update(UI.UpdateEvent.UpdateCandidates(candidates))
            return
        }

        if (key == VNKeys.keyBackspace) {
            if (fullSequence.isNotEmpty()) {
                fullSequence.apply { deleteAt(lastIndex) }
            } else {
                ui.inputConnection.deleteSurroundingText(1, 0)
                return
            }
        } else {
            fullSequence.append(key.symbol)
        }
        val _candidates = linkedSetOf<String>()
        val _prefixes = linkedSetOf<String>()
        if (fullSequence.length == 1) {
            // Only start searching from 2nd key to prevent too many candidates
            //_candidates.addAll(key.subChars.map { "$it" })
            if (key == VNKeys.keyBackspace) {
                prefixes = prefixesCache[fullSequence.toString()] ?: emptySet()
            } else /*key!=Backspace*/{
              key.subChars.map { "$it" }.forEach { c ->
                  if (trie.containsPrefix(c)) {
                      _candidates.add(c)
                      _prefixes.add(c)
                  }
              }
              prefixes = _prefixes
              prefixesCache[fullSequence.toString()] = _prefixes
            }
        } else {
            if (key == VNKeys.keyBackspace) {
                prefixes = prefixesCache[fullSequence.toString()] ?: emptySet()
            } else /*key!=Backspace*/{
                prefixes.forEach { pf ->
                    key.subChars.forEach { sc ->
                        if (trie.containsPrefix(pf + sc)) {
                            _prefixes.add(pf + sc)
                        }
                    }
                }
                prefixes = _prefixes
                prefixesCache[fullSequence.toString()] = _prefixes
            }
        }
        prefixes.forEach { pf ->
            if (trie.containsPrefix(pf)) {
                _candidates.addAll(
                    trie.prefixSearch(pf)
                )
            }
        }
        candidates = CandidateSelection.from(
            _candidates
                .groupBy { it.length }
                .values.flatten()
                // Put number sequence as the last candidate.
                .toMutableList().also {
                  it.add(fullSequence.toString())
                }
        )
        ui.update(UI.UpdateEvent.UpdateCandidates(candidates))
    }

    private fun reset() {
        prefixes = emptySet()
        candidates = CandidateSelection()
        fullSequence.clear()
        //selectedCandidateId=0
    }

}
