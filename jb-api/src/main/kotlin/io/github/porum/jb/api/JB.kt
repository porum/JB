package io.github.porum.jb.api

import android.webkit.WebView

typealias Callback = (message: String) -> Unit

interface JB {
  fun handleJsPostMessage(webView: WebView, payload: String, callback: Callback)
}