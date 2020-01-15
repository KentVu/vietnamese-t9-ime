package com.vutrankien.t9vietnamese

import org.trie4j.patricia.MapPatriciaTrie
import java.io.*
import java.text.Normalizer

val MAGIC:Int = 0xA11600D
private val log:Logging = JavaLog("T9Engine")

class TrieDB(file: File) : DB {

    private val TRIE_FILENAME = "trie"
//    val trie = MapPatriciaTrie<Int>()
    val trie : MapPatriciaTrie<Int>

    private val trieFile = file

    init {
        // The java deserialization thing, y'know :|
        @Suppress("UNCHECKED_CAST")
        trie =
                try {
                    ObjectInputStream(FileInputStream(trieFile))
                            .use { it.readObject() as MapPatriciaTrie<Int> }
                } catch (ex: Exception) {
                    MapPatriciaTrie()
                }
    }

    override fun clear() {
        trieFile.delete()
    }

    override fun put(key: String, value: Int) {
        trie.insert(key, value)
    }

    override fun get(key: String): Int? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {
    }

    private val MAGIC_KEY = "HELO"

    override fun haveMagic(): Boolean {
        return trie.get(MAGIC_KEY) == MAGIC
    }

    override fun putAll(keys: List<String>, defaultValue: Int) {
        keys.forEach { put(it, defaultValue) }
    }

    override fun existingPrefix(prefixes: Set<String>): Set<String> =
            prefixes.flatMap {
                trie.predictiveSearch(it)
            }.toSet()

    override fun putMagic() {
        put(MAGIC_KEY, MAGIC)
    }

    fun readFrom(wordList: WordList) {
        log.i("I: Destroying malicious database and reopen it!")
        clear()
//        wordList.forEachGroup(200) { group ->
        wordList.forEachPercent { percentage, group ->
            putAll(
                    group.map {
                        it.decomposeVietnamese()
                    }
            )
//            Timber.d("just put ${group.size} more words")
            println("$percentage% read, ${group.size} more words")
        }
        // put magic at last to mark database stable (avoid app crash)
        putMagic()
//        val pm:PowerManager
//        pm.newWakeLock(PowerManager.)
        ObjectOutputStream(FileOutputStream(trieFile)).use { it.writeObject(trie) }
        log.d("Persisted trie to file!")
    }

    val initialized: Boolean
            get() = haveMagic()

}

/* 774 0x306 COMBINING BREVE */
const private val BREVE = '̆'
/* 770 0x302 COMBINING CIRCUMFLEX ACCENT */
const private val CIRCUMFLEX_ACCENT = '̂'
/* 795 31B COMBINING HORN */
const private val HORN = '̛'
/* 803 323 COMBINING DOT BELOW */
const private val DOT_BELOW = '̣'

fun String.decomposeVietnamese(): String {
    return Normalizer.normalize(this, Normalizer.Form.NFKD)
            // rearrange intonation and vowel-mark order.
            .replace("([eE])$DOT_BELOW$CIRCUMFLEX_ACCENT".toRegex(), "$1$CIRCUMFLEX_ACCENT$DOT_BELOW")
            // recombine specific vowels.
            .replace(
                    ("([aA][$BREVE$CIRCUMFLEX_ACCENT])|([uUoO]$HORN)|[oOeE]$CIRCUMFLEX_ACCENT").toRegex()
            ) { Normalizer.normalize(it.value, Normalizer.Form.NFKC)}

}
