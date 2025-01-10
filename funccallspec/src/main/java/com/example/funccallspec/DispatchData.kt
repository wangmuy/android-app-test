package com.example.funccallspec

import com.google.devtools.ksp.processing.Dependencies

data class DispatchData(val dependencies: Dependencies, val callStr: String, val schemaStr: String)