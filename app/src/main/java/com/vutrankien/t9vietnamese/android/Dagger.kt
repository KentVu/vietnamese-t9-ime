package com.vutrankien.t9vietnamese.android

import com.vutrankien.t9vietnamese.lib.LogFactory
import com.vutrankien.t9vietnamese.lib.LogModule
import com.vutrankien.t9vietnamese.lib.PresenterModule
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(modules = [PresenterModule::class, LogModule::class])
interface ActivityComponent {
    fun inject(service: T9Vietnamese)
    fun inject(activity: MainActivity)

}

@Module
class AndroidLogModule {
    @Provides
    fun logFactory(): LogFactory =
        AndroidLogFactory()
}
