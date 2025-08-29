package io.github.porum.jb.example.utils

import android.view.View
import io.github.porum.jb.example.R
import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

val View.viewScope: CoroutineScope
  get() {
    val exist = getTag(R.id.tag_view_scope) as? CoroutineScope
    if (exist != null) {
      return exist
    }

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main + ViewAutoDisposeInterceptor(this))
    setTag(R.id.tag_view_scope, scope)
    return scope
  }

private class ViewStateListener(
  private val view: View,
  private val job: Job
) : View.OnAttachStateChangeListener, CompletionHandler {

  override fun onViewAttachedToWindow(v: View) {}

  override fun onViewDetachedFromWindow(v: View) {
    view.removeOnAttachStateChangeListener(this)
    job.cancel()
  }

  override fun invoke(cause: Throwable?) {
    view.removeOnAttachStateChangeListener(this)
    job.cancel()
  }
}

private class ViewAutoDisposeInterceptor(private val view: View) : ContinuationInterceptor {

  override val key: CoroutineContext.Key<*>
    get() = ContinuationInterceptor

  override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
    val job = continuation.context[Job]
    if (job != null) {
      view.addOnAttachStateChangeListener(ViewStateListener(view, job))
    }
    return continuation
  }

}