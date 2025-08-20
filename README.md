## Usage

1. Javascript side

```javascript
window.JB.sendMessage(
  name = 'share',
  payload = {
      message: 'Hi, I share a JB to you.',
  },
  callback = function(resp) {
    console.log(`share ${resp.data}(${resp.code})`);
  }
);
```

2. Android side

```kotlin
@Keep
data class SharePayload(
  val message: String
)

@Name(value = "share")
class ShareBridge : JB<SharePayload> {
  override fun call(context: Context, requestPayload: SharePayload, callback: Callback) {
    // Invokes native android sharing
    val sendIntent: Intent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, requestPayload.message)
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent, null)

    callback(
      ResponsePayload(
        code = 200,
        data = "Thank you share me a JB, I love it."
      )
    )
  }
}
```

That's it, enjoy JB!