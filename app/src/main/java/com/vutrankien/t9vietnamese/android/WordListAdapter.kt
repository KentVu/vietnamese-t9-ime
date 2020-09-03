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

    private var selectedWord = 0

    override fun getItemCount(): Int = words.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.apply {
            text = words[position]
            if (position == selectedWord) {
                isSelected = true
            } else {
                isSelected = false
            }
        }
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
        selectedWord = 0
    }

    fun selectNext() {
        notifyItemChanged(selectedWord)
        selectedWord++
        notifyItemChanged(selectedWord)
    }

    class ViewHolder(cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        val textView: TextView = cardView.findViewById(R.id.content)
    }
}


