package com.example.funccallspec

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Nullability

class FuncCallSpecVisitor(private val processor: FuncCallProcessor): KSVisitorVoid() {
    companion object {
        private const val QNAME_REQUEST = "com.example.sdk.ISkillRequest"
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        classDeclaration.getDeclaredFunctions().forEach { it.accept(this, Unit) }
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        processor.logger.warn(Log.d(FuncCallProcessor.TAG, "function: $function"))

        val dependencies = Dependencies(false, function.containingFile!!)
        val packageName = function.packageName.asString()
        val fileName = "${function.simpleName.asString()}FC".replaceFirstChar { it.uppercaseChar() }
        processor.codeGenerator.createNewFile(dependencies, packageName, fileName).use {file->
            val vars = mutableListOf<String>()
            val assigns = mutableListOf<String>()
            val args = mutableListOf<String>()
            function.parameters.forEach {
                val paramAnno = it.annotations.first()
                when (paramAnno.shortName.asString()) {
                    "SkillCallbackParam" -> {
                        it.name?.asString().also {name->
                            args.add("callback" + if (it.type.resolve().nullability == Nullability.NOT_NULL) "!!" else "")
                        }
                    }
                    "SkillParam" -> {
                        val paramName = paramAnno.arguments.first { arg-> arg.name?.asString() == "name"}.value as String
                        val required = paramAnno.arguments.first { arg-> arg.name?.asString() == "required"}.value as Boolean
                        val paramTypeName = it.type.resolve().declaration.qualifiedName?.asString()
                        vars.add("var $paramName: $paramTypeName? = null")
                        assigns.add("$paramName = if (params.has(\"$paramName\")) params.getString(\"$paramName\") else null")
                        args.add(paramName + (if (required) "!!" else ""))
                    }
                }
            }
            val returnTypeName = function.returnType?.resolve()?.declaration?.qualifiedName?.asString()
            val content = """
package $packageName
import android.os.Bundle
import org.json.JSONObject
import com.example.sdk.ISkillRequest
import com.example.sdk.ISkillCallback

object $fileName {
fun onCall(funcId: String, cmd: String, bundle: Bundle?, callback: ISkillCallback?): ISkillRequest? {
${vars.joinToString("\n    ")}
var ret: ${returnTypeName}? = null
try {
  val params = JSONObject(cmd).getJSONObject("params")
  ${assigns.joinToString("\n      ")}
  ret = ${function.qualifiedName!!.asString()}(${args.joinToString(",")})
  ${if (returnTypeName == QNAME_REQUEST) "return ret" else "return null"}
} catch(e: Exception) {
  val retBundle = Bundle().apply {
    val result = JSONObject()
    result.put("code", 1)
    result.put("msg", e.toString())
    putString("KEY_RSPFORUPSTREAM", result.toString())
  }
  callback?.notify(${if (returnTypeName == QNAME_REQUEST) "ret" else "null"}, 1, retBundle)
  return null
}
}
}
            """.trimIndent()
            file.write(content.toByteArray())

            val skillDefAnno = function.annotations.first()
            val operationId = skillDefAnno.arguments.first { arg-> arg.name?.asString() == "operationId" }.value as String
            processor.dispatches[operationId] = DispatchData(dependencies, "${packageName}.$fileName.onCall(funcId, cmd, bundle, callback)")
        }
    }
}