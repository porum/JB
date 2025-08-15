package io.github.porum.jb.example.lib

import android.content.Context
import android.util.Log
import io.github.porum.jb.api.Name
import io.github.porum.jb.api.Callback
import io.github.porum.jb.api.JB

@Name(value = "network")
class NetworkBridge : JB {
    override fun call(context: Context, message: String, callback: Callback) {
        Log.d("NetworkBridge", "network call")
    }
}