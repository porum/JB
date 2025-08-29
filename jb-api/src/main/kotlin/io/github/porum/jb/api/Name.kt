package io.github.porum.jb.api

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Name(
  val value: String
)