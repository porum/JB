package io.github.porum.jb.example.lib

import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.util.Log
import android.webkit.WebView
import io.github.porum.jb.api.Callback
import io.github.porum.jb.api.JB
import io.github.porum.jb.api.Name
import io.github.porum.jb.api.ResponsePayload

private const val JB_NAME = "read_clipboard"

@Name(value = JB_NAME)
class ReadClipboardBridge : JB<Unit> {
  override fun handleJsPostMessage(webView: WebView, requestPayload: Unit, callback: Callback) {
    Log.w(JB_NAME, "call, request: $requestPayload")

    val clipboard = webView.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    if (clipboard.hasPrimaryClip()) {
      val item = clipboard.primaryClip?.getItemAt(0)
      val text = item?.text
      if (text != null) {
        callback(ResponsePayload(0, text.toString()))
        return
      }
    }

    return callback(ResponsePayload(-1, ""))
  }
}