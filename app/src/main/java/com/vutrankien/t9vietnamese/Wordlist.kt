package com.vutrankien.t9vietnamese

import android.content.Context
import java.io.BufferedReader
import java.io.Reader

/**
 * Created by user on 2018/01/21.
 */
sealed class Wordlist(private val context: Context, private val wordListFile: String) : AutoCloseable {

    private val stream = context.assets.open(wordListFile)
    private val bufferedReader = stream.bufferedReader()

    override fun close() {
        bufferedReader.close()
    }

    private fun nextWord(): String = bufferedReader.readLine()

    class ViVNWordList(context:Context): Wordlist(context, configurations[LOCALE_VN].wordListFile)

    // https://stackoverflow.com/a/6992255
    fun bytesCount(): Int = stream.available()

    fun nextGroup(numEachTransaction: Int) =
            (0..numEachTransaction).map { nextWord()}

    fun forEachGroup(groupSize: Int, block: (List<String>) -> Any) : {
        nextWord().let {  }
        return block((0..groupSize).map { nextWord()})
    }
}
