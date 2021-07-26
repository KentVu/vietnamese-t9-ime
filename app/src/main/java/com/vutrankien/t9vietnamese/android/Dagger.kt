package com.vutrankien.t9vietnamese.android

import android.content.Context
import com.vutrankien.t9vietnamese.engine.T9Engine
import com.vutrankien.t9vietnamese.lib.*
import dagger.Component
import dagger.Module
import dagger.Provides


@Component(modules = [ConfigurationModule::class/*, PresenterModule::class*/, EngineModule::class, AndroidLogModule::class, EnvModule::class])
interface ActivityComponent {
    fun inject(service: T9Vietnamese)
    fun inject(activity: MainActivity)
}

@Deprecated("Presenter is @Injected")
@Module
class PresenterModule() {

    @Provides
    fun presenter(
            engine: T9Engine,
            lg: LogFactory,
            env: Env
    ): Presenter {
        // TODO: Make this configurable
        return Presenter(engine, env, lg)
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

    @Provides
    fun decomposedSeed(): Sequence<String> {
        return sequence {
            context.resources.assets.open("decomposed.dic.sorted").bufferedReader().useLines { lines ->
                lines.forEach { yield(it) }
            }
        }
    }
}


