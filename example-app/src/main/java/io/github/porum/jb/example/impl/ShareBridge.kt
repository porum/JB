package io.github.porum.jb.example.impl

import android.content.Intent
import android.webkit.WebView
import io.github.porum.jb.api.Callback
import io.github.porum.jb.api.JB
import io.github.porum.jb.api.Name
import io.github.porum.jb.api.ResponsePayload
import org.json.JSONObject

@Name(value = "share")
class ShareBridge : JB {
  override fun handleJsPostMessage(webView: WebView, requestPayload: String, callback: Callback) {
    val message = JSONObject(requestPayload).optString("message")

    // Invokes native android sharing
    val sendIntent: Intent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, message)
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    webView.context.startActivity(shareIntent, null)

    callback(ResponsePayload(code = 200, data = "Thank you share me a JB, I love it."))
  }
}