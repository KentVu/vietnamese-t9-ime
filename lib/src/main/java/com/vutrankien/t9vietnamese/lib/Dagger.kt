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

package com.vutrankien.t9vietnamese.lib

import com.vutrankien.t9vietnamese.engine.DefaultT9Engine
import com.vutrankien.t9vietnamese.engine.T9Engine
import dagger.Component
import dagger.Module
import dagger.Provides
import java.io.File

@Component(modules = [EngineModule::class, LogModule::class, EnvModule::class])
abstract class EngineComponents {
    abstract val lg: LogFactory
    abstract fun engine(): T9Engine
}

@Module
class LogModule {
    @Provides
    fun logFactory(): LogFactory =
        JavaLogFactory()
}

@Module
class EngineModule() {
    @Provides
    fun engine(
        lg: LogFactory,
        env: Env
    ): T9Engine = DefaultT9Engine(lg, env)
}

@Component(modules = [PresenterModule::class, LogModule::class])
abstract class PresenterComponents {
    abstract fun presenter(): Presenter
}

@Module
class PresenterModule(
        private val engineSeed: Sequence<String>,
        private val engine: T9Engine,
        private val env: Env
) {
    @Provides
    fun presenter(
        lg: LogFactory
    ): Presenter =
        Presenter(lazy { engineSeed }, engine, env, lg)
}

@Module
class EnvModule() {
    @Provides
    fun env(): Env =
            object : Env {
                override fun fileExists(path: String): Boolean =
                        File(path).exists()

                override val workingDir: String
                    get() = "."
            }
}
