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

package com.vutrankien.t9vietnamese.android

import android.content.Context
import com.vutrankien.t9vietnamese.lib.Env
import java.io.File

/** Should be the [ApplicationContext][Context.getApplicationContext]. */
class AndroidEnv(
    private val context: Context
) : Env {
    // https://stackoverflow.com/q/9713981/1562087
    override fun fileExists(path: String): Boolean =
        //context.getFileStreamPath(path).exists()
        File(path).exists()

    override val workingDir: String
        get() = context.filesDir.absolutePath
}
