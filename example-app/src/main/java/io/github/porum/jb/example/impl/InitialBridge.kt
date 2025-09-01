package io.github.porum.jb.example.impl

import android.util.Log
import android.webkit.WebView
import io.github.porum.jb.api.Callback
import io.github.porum.jb.api.JB
import io.github.porum.jb.api.Name
import io.github.porum.jb.core.postMessage
import io.github.porum.jb.example.utils.viewScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val TAG = "JBInitialized"

@Name(value = "JBInitialized")
class InitialBridge : JB {
  override fun handleJsPostMessage(webView: WebView, payload: String, callback: Callback) {
    Log.d(TAG, "initialized")

    webView.viewScope.launch {
      for (i in 0..1000) {
        webView.postMessage(
          name = "post_bt_rssi",
          payload = "${randomRssi()}",
          callback = { response -> Log.d(TAG, "post_bt_rssi resp: $response") }
        )

        delay(1000)
      }
    }
  }

  private fun randomRssi(): Int {
    val min = -100
    val max = -20
    val range = max - min + 1
    return min + Random.nextInt(range)
  }
}