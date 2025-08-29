package io.github.porum.jb.api

import android.webkit.WebView
import com.google.gson.internal.GsonTypes
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

typealias Callback = (response: ResponsePayload) -> Unit

interface JB<T> {
  fun handleJsPostMessage(webView: WebView, requestPayload: T, callback: Callback)
}

fun genericType(jb: JB<*>): Type {
  val parameterizedType = jb::class.java.genericInterfaces[0] as ParameterizedType
  val genericType = GsonTypes.canonicalize(parameterizedType.actualTypeArguments[0])
  return genericType
}