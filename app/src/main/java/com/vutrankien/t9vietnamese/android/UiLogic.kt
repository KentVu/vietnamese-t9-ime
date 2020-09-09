package com.vutrankien.t9vietnamese.android

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Extraction of common code shared between [MainActivity] and the [T9Vietnamese] IME service.
 */
interface UiLogic {
    companion object {
        private const val AUTO_SCROLL_CANDIDATE = false
    }

    fun updateCandidates(candidates: Collection<String>)
    fun clearCandidates()
    fun initializeCandidatesView(recyclerView: RecyclerView)
    fun nextCandidate()

    class DefaultUiLogic(private val preferences: Preferences) : UiLogic {
        private lateinit var candidatesView: RecyclerView
        private val wordListAdapter = WordListAdapter()

        override fun initializeCandidatesView(recyclerView: RecyclerView) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(recyclerView.context, RecyclerView.HORIZONTAL, false)
                adapter = wordListAdapter
            }
            candidatesView = recyclerView
        }

        override fun updateCandidates(candidates: Collection<String>) {
            wordListAdapter.update(candidates)
        }

        override fun nextCandidate() {
            wordListAdapter.selectNext()
            @Suppress("ConstantConditionIf")
            if (preferences.autoScroll) {
                //candidatesView.scrollToPosition(wordListAdapter.selectedWord)
                // check candidates_view.xml
                (candidatesView.layoutManager as LinearLayoutManager).run {
                    val selectedWord = wordListAdapter.selectedWord
                    if (selectedWord !in findFirstCompletelyVisibleItemPosition()..findLastCompletelyVisibleItemPosition()) {
                        scrollToPositionWithOffset(selectedWord, 20)
                    }
                }
            }
        }

        override fun clearCandidates() {
            wordListAdapter.clear()
        }
    }
}
