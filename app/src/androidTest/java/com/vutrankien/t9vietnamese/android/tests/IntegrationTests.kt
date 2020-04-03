/*
 * Vietnamese-t9-ime: T9 input method for Vietnamese.
 * Copyright (C) 2020 Vu Tran Kien.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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