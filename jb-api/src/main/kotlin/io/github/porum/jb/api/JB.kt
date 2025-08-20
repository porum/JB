package io.github.porum.jb.api

import android.content.Context

typealias Callback = (response: ResponsePayload) -> Unit

interface JB {
    fun call(context: Context, requestPayload: String, callback: Callback)
}