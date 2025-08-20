package io.github.porum.jb.example.impl

import android.content.Context
import android.util.Log
import io.github.porum.jb.api.Name
import io.github.porum.jb.api.Callback
import io.github.porum.jb.api.JB

@Name(value = "JBInitialized")
class InitialBridge : JB<Unit> {
    override fun call(context: Context, requestPayload: Unit, callback: Callback) {
        Log.d("InitialBridge", "initialized: $requestPayload")
    }
}