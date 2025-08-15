package io.github.porum.jb.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import io.github.porum.jb.api.Name

class JBProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLoggerWrapper,
) : SymbolProcessor {

    private val metadataList: MutableList<Metadata> = mutableListOf()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("process")
        resolver.getSymbolsWithAnnotation(Name::class.qualifiedName!!)
            .filter { it is KSClassDeclaration && it.validate() }
            .forEach { it.accept(MetadataCollector(metadataList, logger), Unit) }

        return emptyList()
    }

    override fun finish() {
        super.finish()
        logger.info("finish")
        JBGenerator(codeGenerator).generate(metadataList)
    }
}