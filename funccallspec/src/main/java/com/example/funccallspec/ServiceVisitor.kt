package com.example.funccallspec

import com.example.funccallspec.FuncCallProcessor.Companion.TAG
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid

class ServiceVisitor(
    private val processor: FuncCallProcessor,
): KSVisitorVoid() {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        processor.logger.warn(Log.d(TAG, "ServiceVisitor visitClassDeclaration"))
        val dependencies = Dependencies(true, *processor.dispatches.map { it.value.dependencies }.flatMap { it.originatingFiles }.toTypedArray())
        processor.logger.warn(Log.d(TAG, "ServiceVisitor depens.size=${dependencies.originatingFiles.size}"))
        val packageName = "com.example.funccallspec"
        val fileName = "FuncCallService"
        processor.codeGenerator.createNewFile(dependencies, packageName, fileName).use { file ->
            val dispatchBlocks = processor.dispatches.map { """
"${it.key}" -> {
  return ${it.value.callStr}
}
            """.trimIndent() }.toList()

            val content = """
package $packageName
import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.content.Context
import android.text.TextUtils
import org.json.JSONObject
import com.example.sdk.ISkillRequest
import com.example.sdk.ISkillCallback
import com.example.sdk.ISkillService

class $fileName: Service(), ${classDeclaration.qualifiedName?.asString()} {
    companion object {
        private const val TAG = "${fileName}TAG"
    }
    private val mBinder = object: ISkillService.Stub() {
        override fun call(funcId: String, cmd: String, bundle: Bundle?, callback: ISkillCallback?): ISkillRequest? {
            val ids = funcId.split("/")
            val serviceId = ids[0]
            val operationId = ids[1]
            if (!TextUtils.equals(serviceId, packageName)) {
               val retBundle = Bundle().apply {
                   val result = JSONObject()
                   result.put("code", 1)
                   result.put("msg", "serviceId not match, this is ${'$'}packageName")
                   putString("KEY_RSPFORUPSTREAM", result.toString())
               }
               callback?.notify(null, 1, retBundle)
               return null
            }
            when (operationId) {
${dispatchBlocks.joinToString("\n")}
            }
            val retBundle = Bundle().apply {
                val result = JSONObject()
                result.put("code", 1)
                result.put("msg", "operationId not found for ${'$'}operationId")
                putString("KEY_RSPFORUPSTREAM", result.toString())
            }
               callback?.notify(null, 1, retBundle)
               return null
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged newConfig=${'$'}newConfig")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind intent=${'$'}intent")
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind intent=${'$'}intent")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(TAG, "onRebind intent=${'$'}intent")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "onTaskRemoved rootIntent=${'$'}rootIntent")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onTrimMemory(level: Int) {
        Log.d(TAG, "onTrimMemory level=${'$'}level")
        super.onTrimMemory(level)
    }

    override fun onLowMemory() {
        Log.d(TAG, "onLowMemory")
        super.onLowMemory()
    }

    override fun onStartCommand(p0: Intent?, p1: Int, p2: Int): Int {
        return START_NOT_STICKY
    }
}
            """.trimIndent()
            file.write(content.toByteArray())
        }

        val path = processor.options["generatedResourceDir"] ?: "funcCallSchemas.json"
        processor.codeGenerator.createNewFileByPath(dependencies, path, extensionName = "").use {file->
            val funcBlocks = processor.dispatches.map { it.value.schemaStr }
            val content = """
{
  "functions": [
${funcBlocks.joinToString(",\n")}
  ]
}
            """.trimIndent()
            file.write(content.toByteArray())
        }
    }
}