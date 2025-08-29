package io.github.porum.jb.processor

import com.google.devtools.ksp.symbol.KSFile

data class Metadata(
  val bridgeName: String,
  val className: String,
  val sourceFile: KSFile,
)
