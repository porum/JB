package io.github.porum.jb.api

import android.webkit.WebView

typealias Callback = (response: ResponsePayload) -> Unit

interface JB {
  fun handleJsPostMessage(webView: WebView, requestPayload: String, callback: Callback)
}