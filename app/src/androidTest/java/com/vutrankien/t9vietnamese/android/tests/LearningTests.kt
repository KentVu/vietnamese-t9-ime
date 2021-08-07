package com.vutrankien.t9vietnamese.android.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vutrankien.t9vietnamese.android.AndroidEnv
import com.vutrankien.t9vietnamese.android.AndroidLogFactory
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LearningTests
    : IntegrationBaseTest(AndroidLogFactory.newLog("IntegrationTests")) {

    @Test
    fun env_fileExists() {
        AndroidEnv(InstrumentationRegistry.getInstrumentation().targetContext).fileExists("./T9Engine.dawg")
                .let {
                    log.d("env_fileExists: $it")
                }
    }

    @Test
    fun getResources() {
        //val resourceFiles = ResourcesHelper.getResourceFiles("/*")
        val resourceFiles = LearningTests::class.java.classLoader.getResources("vi-DauMoi.dic")
        //val resourceFiles = ResourcesHelper.getResources(Pattern.compile("dic"))
        log.d("res:$resourceFiles")
        for (res in resourceFiles) {
            log.d(res.toString())
        }
    }
}