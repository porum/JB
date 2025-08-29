package io.github.porum.jb.example.impl

import android.content.Intent
import android.webkit.WebView
import androidx.annotation.Keep
import io.github.porum.jb.api.Callback
import io.github.porum.jb.api.JB
import io.github.porum.jb.api.Name
import io.github.porum.jb.api.ResponsePayload

@Name(value = "share")
class ShareBridge : JB<SharePayload> {
  override fun handleJsPostMessage(
    webView: WebView,
    requestPayload: SharePayload,
    callback: Callback
  ) {
    // Invokes native android sharing
    val sendIntent: Intent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, requestPayload.message)
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    webView.context.startActivity(shareIntent, null)

    callback(ResponsePayload())
  }
}

@Keep
data class SharePayload(
  val message: String
)