package com.github.kentvu.t9vietnamese.web

import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.jetbrains.compose.resources.MissingResourceException
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.files.Blob
import kotlin.js.Promise

object TestBrowserEnvironmentInteraction :
EnvironmentInteraction by BrowserEnvironmentInteraction {
  /*override suspend fun readVnTrie(): ByteArray {
    TODO("Not yet implemented")
  }*/
  private suspend fun readAsBlob(path: String): Blob {
    val resPath = /*WebResourcesConfiguration.getResourcePath*/(path)

    val response = window.fetch(resPath).await()
    if (!response.ok) {
      throw MissingResourceException(resPath)
    }
    return response.blob().await()
  }

  private suspend fun Blob.asByteArray(): ByteArray {
    //https://developer.mozilla.org/en-US/docs/Web/API/Blob/arrayBuffer
    val buffer = asDynamic().arrayBuffer() as Promise<ArrayBuffer>
    return Int8Array(buffer.await()).unsafeCast<ByteArray>()
  }
}
