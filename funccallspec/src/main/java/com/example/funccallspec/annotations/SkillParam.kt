package com.example.funccallspec.annotations

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class SkillParam(
    val name: String,
    val description: String,
    val required: Boolean,
)
