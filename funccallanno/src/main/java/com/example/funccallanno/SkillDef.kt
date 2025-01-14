package com.example.funccallanno

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class SkillDef(
    val serviceId: String,
    val operationId: String,
    val description: String,
    val versionCode: Int,
)
