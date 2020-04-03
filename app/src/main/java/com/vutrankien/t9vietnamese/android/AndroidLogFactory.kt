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

import android.util.Log
import com.vutrankien.t9vietnamese.lib.LogFactory

class AndroidLogFactory: LogFactory {
    override fun newLog(tag: String): LogFactory.Log {
        return object : LogFactory.Log {
            override fun v(msg: String) {
                Log.v(tag, msg)
            }

            override fun d(msg: String) {
                Log.d(tag, msg)
            }

            override fun i(msg: String) {
                Log.i(tag, msg)
            }

            override fun w(msg: String) {
                Log.w(tag, msg)
            }

        }
    }
}