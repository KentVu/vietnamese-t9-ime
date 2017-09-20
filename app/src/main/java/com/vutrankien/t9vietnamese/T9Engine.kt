package com.vutrankien.t9vietnamese

import android.content.Context
import com.snappydb.DB
import com.snappydb.DBFactory
import com.snappydb.SnappydbException
import org.intellij.lang.annotations.Pattern
import timber.log.Timber
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private val log = KLog("T9Engine")

val LOCALE_VN = "vi-VN"
val LOCALE_US = "en-US"

class T9Engine @Throws(EnginePromise::class)
constructor(context: Context, locale: String): AutoCloseable {
    var dbWrapper = DBWrapper(context, locale)

    private val MAGIC = "On your mark, sir!"

    init {
        if (isDbValid()) {
            log.i("DB OK!")
        } else {
//            initializeDb()
            throw EnginePromise(this, context)
        }

    }

    fun initializeDb(context: Context) {
        log.i("Destroying malicious database and reopen it!")
        dbWrapper = dbWrapper.recreate()
        // initialize database
        dbWrapper.put("HELO", MAGIC)
        val inputStream = context.assets.open("morphemes.txt")
        inputStream.bufferedReader().forEachLine { dbWrapper.put(it, 0) }
    }

    fun isDbValid(): Boolean {
        // powerful magic entry to check if were talking to the right database
        val magicEntry = try {
            dbWrapper.get("HELO")
        } catch (e: SnappydbException) {
//            Timber.e(e.message)
//            Timber.e(Exception(e))
            Timber.w(e)
            ""
        }
        if (magicEntry != MAGIC) {
            return false
        }
        return true
    }

    override fun close() {
        dbWrapper.close()
    }

    fun candidates(numseq: String): Set<String> {

//        val keys = dbWrapper.findKeys(numseq)

//        return keys.map {
//            numseq2word(it)
//        }.filter { it != "" }.toSet()
//        return dbWrapper.findKeys(numseq2word(numseq)).toSet()
        return with(numseq2word(numseq)){
            this?.let {
                dbWrapper.findKeys(this).toSet()
            } ?: emptySet()
        }
    }

    private fun numseq2word(@Pattern(value= """^\d+""") numseq: String): String? {
        /* TODO #3 */
//        mapOf<String, String>("2" to "abc")
        return mapOf<String, String>("24236" to "chào").get(numseq)
    }
}

class EnginePromise(private val t9Engine: T9Engine, val context: Context) : Exception() {
    fun getBlocking(): T9Engine {
        t9Engine.initializeDb(context)
        return t9Engine
    }

}

class DBWrapper(private val context: Context, private val locale: String) : DB by DBFactory.open(context) {
//    var snappydb = DBFactory.open(context, locale)

    init {
    }

    fun recreate(): DBWrapper {
        destroy()
        val newWrapper = DBWrapper(context, locale)
        return newWrapper
    }

}

data class PadConfiguration(
        val num1: Set<Char>,
        val num2: Set<Char>,
        val num3: Set<Char>,
        val num4: Set<Char>,
        val num5: Set<Char>,
        val num6: Set<Char>,
        val num7: Set<Char>,
        val num8: Set<Char>,
        val num9: Set<Char>,
        val num0: Set<Char>
)
//object StandardConfiguration: PadConfiguration(num1 = setOf('a', 'b', 'c'))
//val StandardConfiguration = PadConfiguration(num1 = setOf('a', 'b', 'c'))
val configurations =
        mapOf<String, PadConfiguration>(
                LOCALE_VN to PadConfiguration(
                        num2 = linkedSetOf('a', 'ă', 'â', 'b', 'c'),
                        num3 = linkedSetOf('d', 'đ', 'e', 'ê', 'f'),
                        num4 = linkedSetOf('g', 'h', 'i', 'ê', 'f'),
                        num5 = linkedSetOf('j', 'k', 'l', 'ê', 'f'),
                        num6 = linkedSetOf('m', 'n', 'o', 'ê', 'f'),
                        num7 = linkedSetOf('p', 'q', 'r', 's', 'f'),
                        num8 = linkedSetOf('t', 'u', 'v', 'ê', 'f'),
                        num9 = linkedSetOf('w', 'x', 'y', 'z', 'f'),
                        num0 = linkedSetOf('d', 'đ', 'e', 'ê', 'f'),
                        num1 = linkedSetOf('d', 'đ', 'e', 'ê', 'f')
                )
        )

class T9EngineFactory(context: Context) {
    val viVNEngine: T9Engine by lazy { T9Engine(context, LOCALE_VN) }
    val enUSEngine: T9Engine by lazy { T9Engine(context, LOCALE_US) }
}

//val viVNEngine: T9Engine by LazyT9Engine(context, "vi-VN")

var viVNEngine: T9Engine? = null
var enUSEngine: T9Engine? = null

// TODO Asynchronize this
fun Context.getEngineFor(locale: String): T9Engine {
    return when (locale) {
        LOCALE_VN -> viVNEngine ?: run {
            viVNEngine = T9Engine(this, LOCALE_VN)
            viVNEngine!!
        }
        LOCALE_US -> enUSEngine ?: run {
            enUSEngine = T9Engine(this, LOCALE_US)
            enUSEngine!!
        }
        else -> throw UnsupportedOperationException()

//            if (viVNEngine != null) viVNEngine!! else {
//                viVNEngine = T9Engine(this, "vi-VN"); viVNEngine!!
//            }

    }
}