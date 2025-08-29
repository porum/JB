package io.github.porum.jb.example.lib

import android.util.Log
import android.webkit.WebView
import io.github.porum.jb.api.Name
import io.github.porum.jb.api.Callback
import io.github.porum.jb.api.JB

@Name(value = "network")
class NetworkBridge : JB<Unit> {
    override fun handleJsPostMessage(webView: WebView, requestPayload: Unit, callback: Callback) {
        Log.d("NetworkBridge", "network call")
    }
}