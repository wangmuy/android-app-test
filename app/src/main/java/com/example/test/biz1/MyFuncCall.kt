package com.example.test.biz1

import android.os.Bundle
import android.util.Log
import com.example.funccallspec.annotations.SkillCallbackParam
import com.example.funccallspec.annotations.SkillDef
import com.example.funccallspec.annotations.SkillParam
import com.example.sdk.ISkillCallback
import com.example.sdk.ISkillRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

object MyFuncCall {
    private const val TAG = "MyFuncCall"
    const val CALL_1 = "myCall1"
    const val CALL_2 = "myCall2"

    @SkillDef(serviceId = "com.example.test", operationId = CALL_1, description = "this is my function call", versionCode = 1)
    fun myCall1(
        @SkillParam(name="param1", description = "param1", required = true) param1: String,
        @SkillCallbackParam callback: ISkillCallback?,
        @SkillParam(name="param2", description = "param2", required = false) param2: String?,
    ): String {
        val ret = "$param1==$param2"
        val retBundle = Bundle().apply {
            val result = JSONObject()
            result.put("code", 0)
            result.put("msg", ret)
            putString("KEY_RSPFORUPSTREAM", result.toString())
        }
        callback?.notify(null, 0, retBundle)
        return ret
    }

    @SkillDef(serviceId = "com.example.test", operationId = CALL_2, description = "this myCall2", versionCode = 1)
    fun myCall2(
        @SkillParam(name="param1", description = "param1", required = true) param1: String,
        @SkillParam(name="param2", description = "param2", required = false) param2: String?,
        @SkillCallbackParam callback: ISkillCallback
    ): ISkillRequest? {
        val request = object: ISkillRequest.Stub() {
            override fun cancel(reason: String?) {
                Log.d(TAG, "cancel")
            }

            override fun inform(isComplete: Boolean, info: Bundle?) {
                Log.d(TAG, "inform")
            }
        }
        GlobalScope.launch {
            callback.onStatusChanged(request, 1)
            delay(1000)
            callback.onProgress(request, 50, 100)
            delay(1000)
            callback.onProgress(request, 100, 100)
            delay(1000)
            val retBundle = Bundle().apply {
                val result = JSONObject()
                result.put("code", 0)
                result.put("msg", "$param1==yoyo==$param2")
                putString("KEY_RSPFORUPSTREAM", result.toString())
            }
            callback.notify(null, 0, retBundle)
        }
        return request
    }
}