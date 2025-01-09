package com.example.funccallspec

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class FuncCallAnnoProvider: SymbolProcessorProvider {
    companion object {
        private const val TAG = "FuncCallAnnoProvider"
    }
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        Log.d(TAG, "create")
        return FuncCallProcessor(environment.options, environment.logger, environment.codeGenerator)
    }
}