package io.github.porum.jb.example.impl

import android.content.Context
import android.content.Intent
import io.github.porum.jb.api.Name
import io.github.porum.jb.api.Callback
import io.github.porum.jb.api.JB
import io.github.porum.jb.api.Response

@Name(value = "share")
class ShareBridge : JB {
    override fun call(context: Context, message: String, callback: Callback) {
        // Invokes native android sharing
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent, null)

        callback(Response())
    }
}