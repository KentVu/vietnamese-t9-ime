package com.vutrankien.t9vietnamese.android.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vutrankien.t9vietnamese.android.AndroidEnv
import com.vutrankien.t9vietnamese.android.AndroidLogFactory
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IntegrationTests {
    private val log = AndroidLogFactory().newLog("IntegrationTests")

    @Test
    fun env_fileExists() {
        AndroidEnv(InstrumentationRegistry.getInstrumentation().targetContext).fileExists("./T9Engine.dawg")
            .let {
                log.d("env_fileExists: $it")
            }
    }
}