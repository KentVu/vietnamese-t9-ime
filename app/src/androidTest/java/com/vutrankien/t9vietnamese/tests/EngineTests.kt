package com.vutrankien.t9vietnamese.tests

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vutrankien.t9vietnamese.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

/**
 * If test fail retry after adb pm clear com.vutrankien.t9vietnamese
 */
@RunWith(AndroidJUnit4::class)
class EngineTests {
//    @Rule
//    val mActivityRule = ActivityTestRule(MainActivity::class.java)
    lateinit var context: Context
    lateinit var viVnEngine: T9Engine
    init {

    }
    @Before
    fun setUp() {
//        context = mActivityRule.activity
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val locale = "vi-VN"
        val trieDB = TrieDB(context.getFileStreamPath(VNConfiguration.dbname))
        if (!trieDB.initialized) {
            trieDB.readFrom(Wordlist.ViVNWordList(context))
        }
        viVnEngine = T9Engine(locale, trieDB)
//        try {
//            viVnEngine = context.getEngineFor("vi-VN")
//        } catch(e: EngineUninitializedException) {
//            viVnEngine = T9Wordlist(context, TrieDB(context.getFileStreamPath(VNConfiguration.dbname)), LOCALE_VN).initializeThenGetBlocking()
//        }
    }

    @Test
    @Ignore("TODO: Not ready yet")
    fun testNumToStringViVN() {
        // new Locale.Builder().setLanguageTag("vi").build()
        val testCases = mapOf(
                "24236" to arrayOf("chào"),
                "864" to arrayOf("tôi"),
                "6444367" to arrayOf("nghiệp"),
                "6489356" to arrayOf("nguyễn"))

        testEngineNumToString(testCases, viVnEngine)
        viVnEngine.close()
    }

    // Temporarily disable this test, concentrating on vi-VN locale
//    @Test
    fun testNumToStringEnUsLocale() {
        val testCases = mapOf("43556" to arrayOf("hello"),
                "362867" to arrayOf("doctor"))
        val engine = context.getEngineFor("en-US")
        testEngineNumToString(testCases, engine)
        engine.close()
    }

    private fun testEngineNumToString(testCases: Map<String, Array<String>>, engine: T9Engine) {
        for ((key, value) in testCases) {
            engine.input(key)
            val candidateStrings = engine.currentCandidates
            // TODO make engine state-less?
            //  val candidateStrings = engine.candidatesFor(key)
            for (cand in value) {
                assertTrue(
                    "[$key] not containing [$cand], all candidates:$candidateStrings",
                        candidateStrings.contains(cand))
                engine.flush()
            }
        }
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {

    }
}

private fun T9Engine.input(numseq: String) {
    numseq.forEach { input(it) }
}
