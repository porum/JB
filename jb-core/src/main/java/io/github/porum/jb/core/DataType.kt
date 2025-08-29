package io.github.porum.jb.core

import androidx.annotation.IntDef

object DataType {
  /**
   * js post message to native
   */
  const val TYPE_JS_POST = 0

  /**
   * native reply message to js
   */
  const val TYPE_NATIVE_REPLY = 1

  /**
   * native post message to js
   */
  const val TYPE_NATIVE_POST = 2

  /**
   * js reply message to native
   */
  const val TYPE_JS_REPLY = 3

  @JvmStatic
  @Type
  fun toDataType(ordinal: Int): Int =
    when (ordinal) {
      TYPE_JS_POST -> TYPE_JS_POST
      TYPE_NATIVE_REPLY -> TYPE_NATIVE_REPLY
      TYPE_NATIVE_POST -> TYPE_NATIVE_POST
      TYPE_JS_REPLY -> TYPE_JS_REPLY
      else -> TYPE_JS_POST
    }

  @Retention(AnnotationRetention.SOURCE)
  @IntDef(
    TYPE_JS_POST,
    TYPE_NATIVE_REPLY,
    TYPE_NATIVE_POST,
    TYPE_JS_REPLY
  )
  annotation class Type
}