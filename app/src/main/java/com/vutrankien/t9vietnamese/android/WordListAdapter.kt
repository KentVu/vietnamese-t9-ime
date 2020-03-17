package com.vutrankien.t9vietnamese.android

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by user on 2018/03/21.
 */
class WordListAdapter() : RecyclerView.Adapter<WordListAdapter.ViewHolder>() {
    val words = mutableListOf<String>()

    override fun getItemCount(): Int = words.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = words[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            TextView(
                parent.context
            )
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

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
}


