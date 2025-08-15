package io.github.porum.jb.api

import android.content.Context

typealias Callback = (resp: Response) -> Unit

interface JB {
    fun call(context: Context, message: String, callback: Callback)
}