package com.example.funccallspec

import com.example.funccallspec.annotations.SkillDef
import com.example.funccallspec.annotations.SkillService
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate

class FuncCallProcessor(
    internal val options: Map<String, String>,
    internal val logger: KSPLogger,
    internal val codeGenerator: CodeGenerator,
): SymbolProcessor {
    companion object {
        internal const val TAG = "LogProcessor"

    }
    val funcVisitor = FuncCallSpecVisitor(this)
    val dispatches = mutableMapOf<String, DispatchData>()
    val serviceVisitor = ServiceVisitor(this)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn(Log.d(TAG, "process"))
        val funcSymbols = resolver
            .getSymbolsWithAnnotation(SkillDef::class.qualifiedName!!)
            .filterIsInstance<KSFunctionDeclaration>()
        val serviceSymbols = resolver
            .getSymbolsWithAnnotation(SkillService::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()

        if (!funcSymbols.iterator().hasNext() && !serviceSymbols.iterator().hasNext()) {
            logger.warn(Log.d(TAG, "process empty"))
        }

        funcSymbols.filter{ it.validate() }.forEach {
            logger.warn(Log.d(TAG, "process $it"))
            it.accept(funcVisitor, Unit)
        }
        val unvalidatedFuncSymbols = funcSymbols.filter { !it.validate() }.toList()
        if (unvalidatedFuncSymbols.isNotEmpty()) {
            logger.warn(Log.d(TAG, "process unvalidated=$unvalidatedFuncSymbols"))
            return unvalidatedFuncSymbols + serviceSymbols
        } else {
            serviceSymbols.filter{ it.validate() }.firstOrNull()?.also {
                it.accept(serviceVisitor, Unit)
            }
        }
        val allUnvalidated = unvalidatedFuncSymbols + serviceSymbols.filter { !it.validate() }.toList()
        if (allUnvalidated.isNotEmpty()) {
            logger.warn(Log.d(TAG, "process allUnvalidated=$allUnvalidated"))
        }
        return allUnvalidated
    }

}