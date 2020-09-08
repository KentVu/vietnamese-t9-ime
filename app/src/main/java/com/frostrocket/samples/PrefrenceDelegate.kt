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

package com.frostrocket.samples

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Yet another implementation of Shared Preferences using Delegated Properties
 *
 * Check out https://medium.com/@FrostRocketInc/delegated-shared-preferences-in-kotlin-45b82d6e52d0
 * for a detailed walkthrough.
 *
 * @author Matthew Groves
 */

abstract class PreferenceDelegate<T>(private val applicationContext: Context) : ReadWriteProperty<Any, T> {
    companion object {
        //var dayNightMode by StringPreferenceDelegate(applicationContext.getString(R.string.preference_day_night_mode_key))
        //var firstTimeLaunch by BooleanPreferenceDelegate(applicationContext.getString(R.string.preference_app_launch_key))
    }

    protected val sharedPreferences: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(applicationContext) }
}

class StringPreferenceDelegate(
    ctx: Context,
    private val key: String,
    private val defaultValue: String = ""
) : PreferenceDelegate<String>(ctx) {
    override fun getValue(thisRef: Any, property: KProperty<*>) = sharedPreferences.getString(key, defaultValue)!!
    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) = sharedPreferences.edit { putString(key, value) }
}

class IntPreferenceDelegate(
    ctx: Context,
    private val key: String,
    private val defaultValue: Int = 0
) : PreferenceDelegate<Int>(ctx) {
    override fun getValue(thisRef: Any, property: KProperty<*>) = sharedPreferences.getInt(key, defaultValue)
    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) = sharedPreferences.edit { putInt(key, value) }
}

class LongPreferenceDelegate(
    ctx: Context,
    private val key: String,
    private val defaultValue: Long = 0.toLong()
) : PreferenceDelegate<Long>(ctx) {
    override fun getValue(thisRef: Any, property: KProperty<*>) = sharedPreferences.getLong(key, defaultValue)
    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) = sharedPreferences.edit { putLong(key, value) }
}

class FloatPreferenceDelegate(
    ctx: Context,
    private val key: String,
    private val defaultValue: Float = 0.toFloat()
) : PreferenceDelegate<Float>(ctx) {
    override fun getValue(thisRef: Any, property: KProperty<*>) = sharedPreferences.getFloat(key, defaultValue)
    override fun setValue(thisRef: Any, property: KProperty<*>, value: Float) = sharedPreferences.edit { putFloat(key, value) }
}

class BooleanPreferenceDelegate(
    ctx: Context,
    private val defaultValue: Boolean = false
) : PreferenceDelegate<Boolean>(ctx) {
    override fun getValue(thisRef: Any, property: KProperty<*>) = sharedPreferences.getBoolean(property.name, defaultValue)
    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) = sharedPreferences.edit { putBoolean(property.name, value) }
}