package io.github.porum.jb.example.lib

import android.content.Context
import io.github.porum.jb.api.Name
import io.github.porum.jb.api.Callback
import io.github.porum.jb.api.JB

@Name(value = "clipboard")
class ClipboardBridge : JB {
    override fun call(context: Context, message: String, callback: Callback) {

    }
}