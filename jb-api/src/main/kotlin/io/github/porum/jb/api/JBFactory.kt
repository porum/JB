package io.github.porum.jb.api

interface JBFactory<T> {
  fun getName(): String
  fun getJB(): JB<T>
}