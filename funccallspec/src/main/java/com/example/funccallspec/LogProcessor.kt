package com.example.funccallspec

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid

class LogProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
): SymbolProcessor {
    companion object {
        private const val TAG = "LogProcessor"
    }
    val visitor = LogVisitor(options, logger, codeGenerator)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        Log.d(TAG, "process")
        resolver.getAllFiles().forEach { it.accept(visitor, Unit) }
        return emptyList()
    }

    class LogVisitor(private val options: Map<String, String>,
                     private val logger: KSPLogger,
                     private val codeGenerator: CodeGenerator,
        ): KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            classDeclaration.getDeclaredFunctions().forEach { it.accept(this, Unit) }
        }

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            logger.warn(Log.d(TAG, "function: $function"))
        }

        override fun visitFile(file: KSFile, data: Unit) {
            file.declarations.forEach { it.accept(this, Unit) }
        }
    }
}