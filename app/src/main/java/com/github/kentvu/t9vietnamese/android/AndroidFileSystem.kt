package com.github.kentvu.t9vietnamese.android

import android.content.Context
import okio.*
import okio.Path.Companion.toOkioPath

class AndroidFileSystem(private val ctx: Context) : ForwardingFileSystem(SYSTEM) {
    //override fun sink(file: Path, mustCreate: Boolean): Sink =
    //    super.sink(ctx.filesDir.toOkioPath().div(file), mustCreate)
    //
    //override fun source(file: Path): Source {
    //    return super.source(ctx.filesDir.toOkioPath().div(file))
    //}
    override fun onPathParameter(path: Path, functionName: String, parameterName: String): Path {
        return ctx.filesDir.toOkioPath().div(path)
    }
}
