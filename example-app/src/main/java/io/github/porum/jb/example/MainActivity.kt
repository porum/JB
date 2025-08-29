package io.github.porum.jb.example

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebSettingsCompat.DARK_STRATEGY_PREFER_WEB_THEME_OVER_USER_AGENT_DARKENING
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewFeature
import io.github.porum.jb.core.createJsObject
import io.github.porum.jb.example.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
  // Creating the custom WebView Client Class
  private class MyWebViewClient(private val assetLoader: WebViewAssetLoader) :
    WebViewClientCompat() {
    override fun shouldInterceptRequest(
      view: WebView,
      request: WebResourceRequest
    ): WebResourceResponse? {
      return assetLoader.shouldInterceptRequest(request.url)
    }

    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
      super.onPageStarted(view, url, favicon)
      val bridgeJsCode = view.context.assets.open("bridge.js").bufferedReader().readText()
      view.loadUrl("javascript:$bridgeJsCode")
    }
  }

  @SuppressLint("SetJavaScriptEnabled")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }

    val allowedOriginRules = setOf("https://panda912.com")

    // Configuring Dark Theme
    // *NOTE* : The force dark setting is not persistent. You must call the static
    // method every time your app process is started.
    // *NOTE* : The change from day<->night mode is a
    // configuration change so by default the activity will be restarted
    // (and pickup the new values to apply the theme). Take care when overriding this
    //  default behavior to ensure this method is still called when changes are made.
    val nightModeFlag = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    // Check if the system is set to light or dark mode
    if (nightModeFlag == Configuration.UI_MODE_NIGHT_YES) {
      // Switch WebView to dark mode; uses default dark theme
      if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
        WebSettingsCompat.setForceDark(
          binding.webview.settings,
          WebSettingsCompat.FORCE_DARK_ON
        )
      }

      /* Set how WebView content should be darkened. There are three options for how to darken
       * a WebView.
       * PREFER_WEB_THEME_OVER_USER_AGENT_DARKENING- checks for the "color-scheme" <meta> tag.
       * If present, it uses media queries. If absent, it applies user-agent (automatic)
       * darkening DARK_STRATEGY_WEB_THEME_DARKENING_ONLY - uses media queries always, even
       * if there's no "color-scheme" <meta> tag present.
       * DARK_STRATEGY_USER_AGENT_DARKENING_ONLY - it ignores web page theme and always
       * applies user-agent (automatic) darkening.
       * More information about Force Dark Strategy can be found here:
       * https://developer.android.com/reference/androidx/webkit/WebSettingsCompat#setForceDarkStrategy(android.webkit.WebSettings,%20int)
       */
      if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK_STRATEGY)) {
        WebSettingsCompat.setForceDarkStrategy(
          binding.webview.settings,
          DARK_STRATEGY_PREFER_WEB_THEME_OVER_USER_AGENT_DARKENING
        )
      }
    }

    // Set clients
    binding.webview.webViewClient = MyWebViewClient(
      WebViewAssetLoader.Builder()
        .setDomain("panda912.com")
        .addPathHandler(
          "/JB/assets/",
          WebViewAssetLoader.AssetsPathHandler(this)
        )
        .addPathHandler(
          "/JB/res/",
          WebViewAssetLoader.ResourcesPathHandler(this)
        )
        .build()
    )

    // Set Title
    title = getString(R.string.app_name)

    // Setup debugging; See https://developers.google.com/web/tools/chrome-devtools/remote-debugging/webviews for reference
    if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
      WebView.setWebContentsDebuggingEnabled(true)
    }

    // Enable Javascript
    binding.webview.settings.javaScriptEnabled = true

    // Create a JS object to be injected into frames; Determines if WebMessageListener
    // or WebAppInterface should be used
    createJsObject(binding.webview, allowedOriginRules)

    // Load the content
    binding.webview.loadUrl("https://panda912.com/JB/assets/index.html")
  }
}