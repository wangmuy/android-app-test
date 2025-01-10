package com.example.funccallspec

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Nullability

class FuncCallSpecVisitor(private val processor: FuncCallProcessor): KSVisitorVoid() {
    companion object {
        private const val QNAME_REQUEST = "com.example.sdk.ISkillRequest"

        private fun getSchemaParamType(paramType: KSType): String {
            return when (paramType.declaration.simpleName.asString()) {
                "String" -> "string"
                "Int" -> "int"
                else -> "string"
            }
        }
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        classDeclaration.getDeclaredFunctions().forEach { it.accept(this, Unit) }
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        processor.logger.warn(Log.d(FuncCallProcessor.TAG, "function: $function"))

        val dependencies = Dependencies(false, function.containingFile!!)
        val packageName = function.packageName.asString()
        val fileName = "${function.simpleName.asString()}FC".replaceFirstChar { it.uppercaseChar() }

        val funcAnno = function.annotations.first()
        val serviceId = funcAnno.arguments.first { arg-> arg.name?.asString() == "serviceId" }.value as String
        val operationId = funcAnno.arguments.first { arg-> arg.name?.asString() == "operationId" }.value as String
        val funcDesc = funcAnno.arguments.first { arg-> arg.name?.asString() ==  "description" }.value as String
        val versionCode = funcAnno.arguments.first { arg-> arg.name?.asString() == "versionCode" }.value as Int

        val vars = mutableListOf<String>()
        val assigns = mutableListOf<String>()
        val args = mutableListOf<String>()
        val schemaParams = mutableListOf<String>()
        val schemaRequires = mutableListOf<String>()
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
                    val paramType = it.type.resolve()
                    val paramTypeName = paramType.declaration.qualifiedName?.asString()
                    vars.add("var $paramName: $paramTypeName? = null")
                    assigns.add("$paramName = if (params.has(\"$paramName\")) params.getString(\"$paramName\") else null")
                    args.add(paramName + (if (required) "!!" else ""))

                    val paramDesc = paramAnno.arguments.first { arg-> arg.name?.asString() ==  "description" }.value as String
                    schemaParams.add("""
"$paramName": {
  "type": "${getSchemaParamType(paramType)}",
  "description": "$paramDesc"
}
                        """.trimIndent())
                    if (required) {
                        schemaRequires.add("\"$paramName\"")
                    }
                }
            }
        }
        val returnTypeName = function.returnType?.resolve()?.declaration?.qualifiedName?.asString()

        processor.codeGenerator.createNewFile(dependencies, packageName, fileName).use {file->
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
        }

        val callStr = "${packageName}.$fileName.onCall(funcId, cmd, bundle, callback)"
        val schemaStr = """
{
  "serviceId": "$serviceId",
  "operationId": "$operationId",
  "description": "$funcDesc",
  "versionCode": $versionCode,
  "parameters": {
    "type": "object",
    "properties": {
      ${schemaParams.joinToString(",\n")}
    },
    "required": [
      ${schemaRequires.joinToString(",")}
    ]
  }
}
        """.trimIndent()

        processor.dispatches[operationId] = DispatchData(dependencies, callStr, schemaStr)
    }
}