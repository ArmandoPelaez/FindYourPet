package com.findyourpet.app.util

import com.google.firebase.crashlytics.FirebaseCrashlytics

object CrashReporter {
    private val allowedKeys = setOf(
        "app_flow",
        "app_state",
        "document_type",
        "backend_state",
        "permission_state"
    )

    private val sensitiveValuePatterns = listOf(
        Regex("""[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}""", RegexOption.IGNORE_CASE),
        Regex("""\+?\d[\d\s().-]{7,}\d"""),
        Regex("""-?\d{1,2}\.\d{4,}\s*,\s*-?\d{1,3}\.\d{4,}"""),
        Regex("""https?://\S+""", RegexOption.IGNORE_CASE),
        Regex("""(?:token|password|key|secret)=\S+""", RegexOption.IGNORE_CASE)
    )

    fun setContext(key: String, value: String) {
        if (key !in allowedKeys) return
        FirebaseCrashlytics.getInstance().setCustomKey(key, sanitizeForCrashMetadata(value))
    }

    fun recordNonFatal(flow: String, state: String, documentType: String, errorType: String) {
        setContext("app_flow", flow)
        setContext("app_state", state)
        setContext("document_type", documentType)
        FirebaseCrashlytics.getInstance().recordException(
            IllegalStateException("Non-fatal $errorType in $flow/$state/$documentType")
        )
    }

    fun sanitizeForCrashMetadata(value: String): String {
        val trimmed = value.trim()
        if (trimmed.isBlank()) return ""
        return if (sensitiveValuePatterns.any { it.containsMatchIn(trimmed) }) {
            "[redacted]"
        } else {
            trimmed.take(80)
        }
    }
}
