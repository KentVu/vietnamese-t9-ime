package com.github.kentvu.t9vietnamese.lib

import com.github.kentvu.t9vietnamese.UI
import com.github.kentvu.t9vietnamese.model.*
import com.github.kentvu.t9vietnamese.model.Action
import com.github.kentvu.t9vietnamese.model.NumericSubstitution
import com.github.kentvu.t9vietnamese.model.VNKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.apply
import kotlin.collections.toMutableList
import kotlin.text.deleteAt
import kotlin.text.isNotEmpty
import kotlin.text.lastIndex
import kotlin.text.map

class Engine(
    private val ui: UI,
    private val trie: Trie,
) {
    private val inputConnection: InputSystemConnection = ui.inputConnection
    private var candidates: CandidateSelection = CandidateSelection()
    private val fullSequence = StringBuilder(10)
    private var prefixes: Set<String> = emptySet()
    private val prefixesCache= mutableMapOf<String, Set<String>>()
    private var shiftMode: Boolean = false

    /*fun type(keySequence: String) {
        keySequence.forEach { k ->
            type(VNKeys.fromChar(k))
        }
    }*/
    fun type(keySequence: List<Action>) {
        keySequence.forEach { key ->
            //if(key.isWordTerminal)
            type(key)
        }
    }

    fun type(action: Action) {
        if (action == Action.Clear) {
            reset()
            ui.update { copy(candidates = this@Engine.candidates) }
            return
        }
        if (action == Action.Star) {
            //ui.update(UI.UpdateEvent.SelectNextCandidate)
            candidates = candidates.advanceSelectedCandidate()
            ui.update { copy(candidates = this@Engine.candidates) }
            return
        }
        if (action == Action.Hash) {
            // Commit fullSequence directly.
            /*ui.inputConnection.commitText(fullSequence.toString())
            reset()
            ui.update(UI.UpdateEvent.UpdateCandidates(candidates))*/
            // Hash button: select the number sequence.
            candidates = candidates.select(candidates.lastIndex())
            ui.update { copy(candidates = this@Engine.candidates) }
            return
        }
        if (action == Action.Space) {
            if (isComposing()) {
                // commit current composing word with a space.
                inputConnection.commitText(candidates.selectedCandidate.text + " ")
            } else {
                inputConnection.commitText("${Action.Space.rawChar}")
            }
            reset()
            ui.update { copy(candidates = this@Engine.candidates) }
            return
        }
        if (action == Action.Ok) {
            if (isComposing()) {
                inputConnection.commitText(candidates.selectedCandidate.text)
            } else {
                inputConnection.performEditorAction()
            }
            reset()
            ui.update { copy(candidates = this@Engine.candidates) }
            return
        }
        if (action == Action.Return) {
            if (isComposing()) {
                // if we're in middle of composing a word, commit without a space.
                inputConnection.commitText(candidates.selectedCandidate.text)
            } else {
                inputConnection.commitText("\n")
            }
            reset()
            ui.update { copy(candidates = this@Engine.candidates) }
            return
        }
        if (action == Action.Zero) {
            fullSequence.append(action.rawChar)
            prefixes = emptySet()
            candidates = CandidateSelection.from(listOf(fullSequence.toString()))
            ui.update { copy(candidates = this@Engine.candidates) }
            return
        }
        if (action == Action.One) {
            if (isComposing()) {
                inputConnection.commitText(candidates.selectedCandidate.text)
                reset()
            }
            fullSequence.append(Action.One.rawChar)
            candidates = CandidateSelection.from(
                buildList {
                    addAll(
                        NumericSubstitution.VN.forNum(Action.One.rawChar!!)
                            .map { "$it" })
                    add("${Action.One.rawChar}")
                })
            ui.update { copy(candidates = this@Engine.candidates) }
            return
            // pass through
        }
        val _candidates = linkedSetOf<String>()
        if (action == Action.Shift) {
            shiftMode = !shiftMode
            if (isComposing()) {
                expandPrefixesTo(_candidates)
                updateCandidateSelection(_candidates, preserveSel = true)
            }
            return
        }

        if (action == Action.Backspace) {
            if (isComposing()) {
                fullSequence.apply { deleteAt(lastIndex) }
                prefixes = prefixesCache[fullSequence.toString()] ?: emptySet()
                expandPrefixesTo(_candidates)
                updateCandidateSelection(_candidates)
            } else {
                inputConnection.deleteSurroundingText(1, 0)
            }
            return
        }

        fullSequence.append(action.rawChar)
        val _prefixes = linkedSetOf<String>()
        // TODO inject subChars
        val subChars = // TODO move to Action
            NumericSubstitution.VN.forNum(
            action.rawChar ?: error("Should be typing action here")
        )
        if (fullSequence.length == 1) {
            // Only start searching from 2nd key to prevent too many candidates
            subChars.map { "$it" }.forEach { c ->
              if (trie.containsPrefix(c)) {
                  _candidates.add(c)
                  _prefixes.add(c)
              }
          }
            prefixes = _prefixes
            prefixesCache[fullSequence.toString()] = _prefixes
        } else {
            prefixes.forEach { pf ->
                subChars.forEach { sc ->
                    if (trie.containsPrefix(pf + sc)) {
                        _prefixes.add(pf + sc)
                    }
                }
            }
            prefixes = _prefixes
            prefixesCache[fullSequence.toString()] = _prefixes
        }
        expandPrefixesTo(_candidates)
        updateCandidateSelection(_candidates)
    }

    private fun expandPrefixesTo(_candidates: LinkedHashSet<String>) {
        prefixes.forEach { pf ->
            if (trie.containsPrefix(pf)) {
                _candidates.addAll(
                    trie.prefixSearch(pf)
                )
            }
        }
    }

    private fun updateCandidateSelection(cands: Set<String>, preserveSel: Boolean = false) {
        candidates = CandidateSelection.from(
            cands
                .groupBy { it.length }
                .values.flatten().run {
                    if (!shiftMode) this
                    else map { it.replaceFirstChar(Char::uppercaseChar) }
                }
                // Put number sequence as the last candidate.
                .toMutableList().also {
                    it.add(fullSequence.toString())
                },
            if (preserveSel) candidates.selectedCandidateId else 0
        )
        ui.update { copy(candidates = this@Engine.candidates) }
    }

    private fun isComposing() = candidates.isNotEmpty()

    private fun reset() {
        prefixes = emptySet()
        candidates = CandidateSelection()
        fullSequence.clear()
        shiftMode = false
    }

    fun selectCandidate(candidateId: Int) {
        candidates = candidates.select(candidateId)
        ui.update {
            copy(
                candidates = this@Engine.candidates
            )
        }
    }

}
