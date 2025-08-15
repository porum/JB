package io.github.porum.jb.example.impl

import android.content.Context
import android.util.Log
import io.github.porum.jb.api.Name
import io.github.porum.jb.api.Callback
import io.github.porum.jb.api.JB

@Name(value = "init")
class InitialBridge : JB {
    override fun call(context: Context, message: String, callback: Callback) {
        Log.d("InitialBridge", "initialized")
    }
}