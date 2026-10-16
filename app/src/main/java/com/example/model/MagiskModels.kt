package com.example.model

data class ModuleItem(
    val id: String,
    val name: String,
    val version: String,
    val author: String,
    val description: String,
    val isEnabled: Boolean = true,
    val isOnline: Boolean = false,
    val downloadUrl: String = "",
    val updateAvailable: Boolean = false
)

data class DenyApp(
    val packageName: String,
    val appName: String,
    val category: String, // Banking, Games, Finance, Social
    val isHidden: Boolean = false
)

data class SuperuserApp(
    val packageName: String,
    val appName: String,
    val uid: Int,
    val isAllowed: Boolean = true,
    val accessCount: Int = 12
)

data class BootScript(
    val name: String,
    val path: String, // /data/adb/post-fs-data.sh or /data/adb/service.sh
    val content: String,
    val isEnabled: Boolean = true
)

data class LogEntry(
    val id: Long = System.currentTimeMillis(),
    val timestamp: String,
    val level: LogLevel, // INFO, WARN, ERROR, SU
    val tag: String,
    val message: String
)

enum class LogLevel {
    INFO, WARN, ERROR, SU
}
