/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.porum.jb.core

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import com.google.gson.GsonBuilder
import io.github.porum.jb.api.JBFactory
import io.github.porum.jb.api.genericType
import org.json.JSONObject
import java.util.ServiceLoader

// the name that will be given to the Javascript objects created by either
// WebMessageListener or JavascriptInterface
private const val jsObjName = "jsObject"

// Create a handler that runs on the UI thread
private val handler: Handler = Handler(Looper.getMainLooper())

private val gson = GsonBuilder().create()

/**
 * Injects a JavaScript object which supports a {@code postMessage()} method.
 * A feature check is used to determine if the preferred API, WebMessageListener, is supported.
 * If it is, then WebMessageListener will be used to create a JavaScript object. The object will be
 * injected into all of the frames that have an origin matching those in {@code allowedOriginRules}.
 * <p>
 * If WebMessageListener is not supported then the method will defer to using JavascriptInterface
 * to create the JavaScript object.
 * <p>
 * The WebMessageListener invokes callbacks on the UI thread by default. However,
 * JavascriptInterface invokes callbacks on a background thread by default. In order to
 * guarantee thread safety and that the caller always gets consistent behavior the the callback
 * should always be called on the UI thread. To change the default behavior of JavascriptInterface,
 * the callback is wrapped in a handler which will tell it to run on the UI thread instead of the default
 * background thread it would otherwise be invoked on.
 * <p>
 * @param webView the component that WebMessageListener or JavascriptInterface will be added to
 * @param allowedOriginRules a set of origins used only by WebMessageListener, if a frame matches an
 * origin in this set then it will have the JS object injected into it
 */
fun createJsObject(webView: WebView, allowedOriginRules: Set<String>) {
    if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
        WebViewCompat.addWebMessageListener(
            webView, jsObjName, allowedOriginRules
        ) { view, message, sourceOrigin, isMainFrame, replyProxy ->
            call(webView, message.data ?: "{}") { response ->
                replyProxy.postMessage(response)
            }
        }
    } else {
        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun postMessage(message: String) {
                // Use the handler to invoke method on UI thread
                handler.post {
                    call(webView, message) { response ->
                        val jsCode = """
                            $jsObjName.onmessage({
                                data: $response
                            });
                        """.trimIndent()
                        webView.evaluateJavascript("javascript:$jsCode", null)
                    }
                }
            }
        }, jsObjName)
    }
}

private val jbMap = ServiceLoader.load(JBFactory::class.java).associate {
    it.getName() to it.getJB()
}

private inline fun call(
    webView: WebView,
    message: String,
    crossinline callback: (response: String) -> Unit
) {
    val request = JSONObject(message)
    val requestId = request.optString("id", "")
    val requestName = request.optString("name", "")
    val requestPayload = request.optString("payload", "")

    val target = jbMap[requestName] ?: return

    target.call(
        webView.context,
        gson.fromJson(requestPayload, genericType(target))
    ) { responsePayload ->
        val response = with(JSONObject()) {
            put("id", requestId)
            put("name", requestName)
            put("payload", with(JSONObject()) {
                put("code", responsePayload.code)
                put("data", responsePayload.data)
            })
        }
        callback(response.toString())
    }
}