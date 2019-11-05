package com.vutrankien.t9vietnamese

import android.content.Context
import timber.log.Timber.d
import java.io.Closeable

/**
 * Created by user on 2018/01/21.
 */
sealed class Wordlist(context: Context, wordListFile: String) : Closeable {

    private val stream = context.assets.open(wordListFile)
    private val bufferedReader = stream.bufferedReader()

    override fun close() {
        bufferedReader.close()
    }

    private fun nextWord(): String = bufferedReader.readLine()

    class ViVNWordList(context:Context): Wordlist(context, configurations.getValue(LOCALE_VN).wordListFile)

    // https://stackoverflow.com/a/6992255
    private fun bytesCount(): Int = stream.available()

    fun nextGroup(numEachTransaction: Int) =
            (0..numEachTransaction).map { nextWord()}

//    fun forEachGroup(groupSize: Int, operation: (progress:Int, Set<String>) -> Any) {
    fun forEachGroup(groupSize: Int, operation: (Set<String>) -> Any) {
        bufferedReader.useLines {
            var count = 0
            it.groupBy {
                count++
                count / groupSize
            }.forEach { operation(it.value.toSet()) }
//            var sequence = it.take(groupSize)
//            do {
//                val group = sequence.toList()
//                group.toSet().let(operation)
//                sequence = sequence.take(groupSize)
//            } while (group.size == groupSize)
        }
//        nextWord().let {  }
//        return operation((0..groupSize).map { nextWord()})
    }

    fun forEachPercent(operation: (percentage:Int, Set<String>) -> Any) {
        val flength = bytesCount()
        bufferedReader.useLines {
            var bytesRead = 0
            it.groupBy {
                val lastPercentage = bytesRead * 100 / flength
                bytesRead += it.toByteArray().size
                val percentage = bytesRead * 100 / flength
                if (lastPercentage != percentage)
                    d("$percentage% read, bytesRead:$bytesRead / " +
                        "$flength")
                percentage
            }.forEach { operation(it.key, it.value.toSet()) }
            //            do {
//                val group = it.take(groupSize).toList()
//                group.toSet().let(operation)
//            } while (group.size == groupSize)
        }
//        nextWord().let {  }
//        return operation((0..groupSize).map { nextWord()})
    }
}
