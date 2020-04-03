/*
 * Vietnamese-t9-ime: T9 input method for Vietnamese.
 * Copyright (C) 2020 Vu Tran Kien.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.vutrankien.t9vietnamese.android

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Extraction of common code shared between [MainActivity] and the [T9Vietnamese] IME service.
 */
interface UiLogic {
    fun updateCandidates(candidates: Collection<String>)

    fun clearCandidates()

    fun initializeCandidatesView(recyclerView: RecyclerView)

    class DefaultUiLogic: UiLogic {
        private val wordListAdapter = WordListAdapter()

        override fun initializeCandidatesView(recyclerView: RecyclerView) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(recyclerView.context, RecyclerView.HORIZONTAL, false)
                adapter = wordListAdapter
            }
        }

        override fun updateCandidates(candidates: Collection<String>) {
            wordListAdapter.update(candidates)
        }

        override fun clearCandidates() {
            wordListAdapter.clear()
        }
    }
}
