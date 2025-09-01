package io.github.porum.jb.example.impl

import android.content.Intent
import android.webkit.WebView
import io.github.porum.jb.api.Callback
import io.github.porum.jb.api.JB
import io.github.porum.jb.api.Name

@Name(value = "share")
class ShareBridge : JB {
  override fun handleJsPostMessage(webView: WebView, payload: String, callback: Callback) {
    // Invokes native android sharing
    val sendIntent: Intent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, payload)
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    webView.context.startActivity(shareIntent, null)

    callback("Thank you share me a JB, I love it.")
  }
}