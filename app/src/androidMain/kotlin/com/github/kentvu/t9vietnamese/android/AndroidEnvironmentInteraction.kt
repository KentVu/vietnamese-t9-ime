package com.github.kentvu.t9vietnamese.android

import android.content.Context
import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okio.FileSystem
import okio.Source
import okio.source
import org.jetbrains.compose.resources.ExperimentalResourceApi
import t9vietnamese.common.generated.resources.Res
import kotlin.text.removePrefix

class AndroidEnvironmentInteraction(private val context: Context) : EnvironmentInteraction {
    override val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main
    override val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO
    override val fileSystem: FileSystem
        get() = AndroidFileSystem(context)
    @OptIn(ExperimentalResourceApi::class)
    override val vnWordsSource: Flow<Result<Source>>
        get() = sourceFrom(Res.getUri("files/vi-DauMoi.dic"))
    @OptIn(ExperimentalResourceApi::class)
    override val vnTrieSource: Flow<Result<Source>>
        get() = sourceFrom(Res.getUri("files/vi-DauMoi.dawg"))

    private fun sourceFrom(resUri: String): Flow<Result<Source>> {
        return flow {
            emit(Result.success(context.assets.open(
                resUri
                    // This Uri is only supported in WebView, there's no way to open the file as stream via compose-resources
                    // https://stackoverflow.com/questions/5030448/android-how-to-find-the-absolute-path-of-the-assets-folder?noredirect=1&lq=1
                    .removePrefix("file:///android_asset/")
            ).source()))
        }.catch { Result.failure<Source>(it) }
    }
}
