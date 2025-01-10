package com.example.funccallspec.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class SkillDef(
    val serviceId: String,
    val operationId: String,
    val description: String,
    val versionCode: Int,
)
