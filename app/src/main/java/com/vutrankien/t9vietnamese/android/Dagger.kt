package com.vutrankien.t9vietnamese.android

import android.content.Context
import android.util.Log
import com.vutrankien.t9vietnamese.engine.T9Engine
import com.vutrankien.t9vietnamese.lib.*
import com.vutrankien.t9vietnamese.lib.VietnameseWordSeed.decomposeVietnamese
import dagger.Component
import dagger.Module
import dagger.Provides


@Component(modules = [ConfigurationModule::class, PresenterModule::class, EngineModule::class, AndroidLogModule::class, EnvModule::class])
interface ActivityComponent {
    fun inject(service: T9Vietnamese)
    fun inject(activity: MainActivity)

}

@Module
class PresenterModule() {

    @Provides
    fun presenter(
            engine: T9Engine,
            lg: LogFactory,
            env: Env,
            seed: Sequence<String>
    ): Presenter {
        // TODO: Make this configurable
        return Presenter(seed, engine, env, lg)
    }
}

@Module
class EnvModule(private val context: Context) {
    @Provides
    fun env(): Env {
        return AndroidEnv(context)
    }
}

@Module
class AndroidLogModule {
    @Provides
    fun logFactory(): LogFactory =
        AndroidLogFactory()
}

@Module
class ConfigurationModule(private val context: Context) {
    @Provides
    fun padConfiguration(): PadConfiguration =
        VnPad

    fun seed(): Sequence<String> {
        Log.d("seed", "making seed")
        return sequence {
            context.resources.assets.open("vi-DauMoi.dic").bufferedReader().useLines { lines ->
                context.openFileOutput("decomposed.dic", Context.MODE_PRIVATE).bufferedWriter().use {writer ->
                    lines.forEach { yield(it.decomposeVietnamese().also { writer.write("$it\n") }) }
                }
            }
        }
    }

    @Provides
    fun decomposedSeed(): Sequence<String> {
        return sequence {
            context.resources.assets.open("decomposed.dic.sorted").bufferedReader().useLines { lines ->
                lines.forEach { yield(it) }
            }
        }
    }
}


