package io.github.porum.jb.processor

import com.google.devtools.ksp.processing.KSPLogger

class KSPLoggerWrapper(private val kspLogger: KSPLogger) {

    private val tag = "[JB][KSP]: "

    fun lifecycle(message: String) {
        kspLogger.logging(tag + message)
    }

    fun info(message: String) {
        kspLogger.info(tag + message)
    }

    fun warn(message: String) {
        kspLogger.warn(tag + message)
    }

    fun error(message: String) {
        kspLogger.error(tag + message)
    }
}