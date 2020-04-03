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

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView

/**
 * Created by user on 2018/03/21.
 */
class WordListAdapter() : RecyclerView.Adapter<WordListAdapter.ViewHolder>() {
    private val words = mutableListOf<String>()

    override fun getItemCount(): Int = words.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = words[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.candidate, parent, false) as CardView
                //.also {
                //(it.layoutParams as ViewGroup.MarginLayoutParams).apply
                //it.layoutParams = ViewGroup.MarginLayoutParams(parent.context) MarginLayoutParamsCompat.also {
                //    leftMargin = parent.resources.getDimensionPixelOffset(R.dimen.candidate_gap)
                //}
        )

    fun clear() {
        words.clear()
        notifyDataSetChanged()
    }

    fun update(cand: Collection<String>) {
        words.clear()
        words.addAll(cand)
        notifyDataSetChanged()
    }

    class ViewHolder(cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        val textView: TextView = cardView.findViewById(R.id.content)
    }
}


