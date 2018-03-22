package com.vutrankien.t9vietnamese.tests

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import com.snappydb.SnappydbException
import com.vutrankien.t9vietnamese.*
import kotlinx.coroutines.experimental.CommonPool

import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
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
        context = InstrumentationRegistry.getTargetContext()
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
    @Throws(SnappydbException::class)
    fun testNumToStringViVN() {
        // new Locale.Builder().setLanguageTag("vi").build()
        val testCases = mapOf(
                "24236" to arrayOf("chào"),
                "864" to arrayOf("tôi"),
                "6444367" to arrayOf("nghiệp"))

        testEngineNumToString(testCases, viVnEngine)
        viVnEngine.close()
    }

    // Temporarily disable this test, concentrating on vi-VN locale
//    @Test
    @Throws(SnappydbException::class)
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
            val candiateStrings = engine.currentCandidates
            for (cand in value) {
                assertTrue(
                        String.format("[%s] not containing [%s], all candidates:%s", key, cand, candiateStrings),
                        candiateStrings.contains(cand))
                engine.flush()
                //Arrays.asList(candiateStrings).contains(cand));
            }
        }
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {

    }
}
