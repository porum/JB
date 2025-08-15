package io.github.porum.jb.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class JBProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val logger = KSPLoggerWrapper(environment.logger)
        val moduleName = environment.options["MODULE_NAME"].toString()

        return JBProcessor(environment.codeGenerator, logger)
    }
}