# JB

Yet another js bridge for Android.

## Usage

### Web --post--> Native --reply--> Web

1. Web Side

```javascript
window.JB.postMessage(
  name = "share",
  payload = "Hi, I share a JB to you.",
  callback = function(resp) {
    console.log(`share ${resp.data}(${resp.code})`);
  }
);
```

2. Native Side

```kotlin
@Name(value = "share")
class ShareBridge : JB {
  override fun handleJsPostMessage(webView: WebView, payload: String, callback: Callback) {
    // Invokes native android sharing
    val sendIntent: Intent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, payload)
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    webView.context.startActivity(shareIntent, null)

    callback(ResponsePayload(code = 0, data = "Thank you share me a JB, I love it."))
  }
}
```

### Native --post--> Web --reply--> Native

1. Native Side

```kotlin
webView.postMessage(
  name = "post_bt_rssi",
  payload = "-50",
  callback = { response -> Log.d(TAG, "post_bt_rssi resp: $response") }
)
```

2. Web Side

```javascript
window.JB.addNativeMessageListener((name, payload, callback) => {
  console.log("receive native post message", name, JSON.stringify(payload));
  if (name === "post_bt_rssi") {
    callback({ code: 0, data: "okay" });
  }
});
```

That's it, enjoy JB!