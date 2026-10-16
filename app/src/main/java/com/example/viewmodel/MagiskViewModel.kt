package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MagiskViewModel : ViewModel() {

    // Authentic Magisk Info
    private val _magiskVersion = MutableStateFlow("27.0")
    val magiskVersion: StateFlow<String> = _magiskVersion.asStateFlow()

    private val _appVersion = MutableStateFlow("27.0 (27000)")
    val appVersion: StateFlow<String> = _appVersion.asStateFlow()

    private val _isRooted = MutableStateFlow(true)
    val isRooted: StateFlow<Boolean> = _isRooted.asStateFlow()

    private val _isEmergencyUnrooted = MutableStateFlow(false)
    val isEmergencyUnrooted: StateFlow<Boolean> = _isEmergencyUnrooted.asStateFlow()

    private val _zygiskEnabled = MutableStateFlow(true)
    val zygiskEnabled: StateFlow<Boolean> = _zygiskEnabled.asStateFlow()

    private val _denylistActive = MutableStateFlow(true)
    val denylistActive: StateFlow<Boolean> = _denylistActive.asStateFlow()

    private val _amoledTheme = MutableStateFlow(false)
    val amoledTheme: StateFlow<Boolean> = _amoledTheme.asStateFlow()

    private val _packageNameStatus = MutableStateFlow("com.topjohnwu.magisk")
    val packageNameStatus: StateFlow<String> = _packageNameStatus.asStateFlow()

    private val _hiddenIconActive = MutableStateFlow(false)
    val hiddenIconActive: StateFlow<Boolean> = _hiddenIconActive.asStateFlow()

    // Superuser Settings & Apps
    private val _suAccessMode = MutableStateFlow("Prompt") // Prompt, Allow, Deny
    val suAccessMode: StateFlow<String> = _suAccessMode.asStateFlow()

    private val _suAutoResponse = MutableStateFlow(false)
    val suAutoResponse: StateFlow<Boolean> = _suAutoResponse.asStateFlow()

    private val _superuserApps = MutableStateFlow(
        listOf(
            SuperuserApp("com.termux", "Termux", 10234, true, 48),
            SuperuserApp("eu.chainfire.supersu", "SuperSU Tool", 10112, true, 5),
            SuperuserApp("com.titaniumbackup", "Titanium Backup", 10089, true, 19),
            SuperuserApp("com.viper.fx", "ViPER4Android", 10201, true, 112)
        )
    )
    val superuserApps: StateFlow<List<SuperuserApp>> = _superuserApps.asStateFlow()

    // DenyList Apps
    private val _denyApps = MutableStateFlow(
        listOf(
            DenyApp("com.rajhi.mubasher", "Al Rajhi Bank", "Banking", true),
            DenyApp("com.ahb.mobile", "Al Ahli Mobile", "Banking", true),
            DenyApp("com.tencent.ig", "PUBG Mobile", "Games", true),
            DenyApp("com.activision.callofduty.shooter", "Call of Duty", "Games", false),
            DenyApp("com.google.android.apps.walletnfcrel", "Google Pay", "Finance", true),
            DenyApp("com.netflix.mediaclient", "Netflix", "Media", false)
        )
    )
    val denyApps: StateFlow<List<DenyApp>> = _denyApps.asStateFlow()

    // Modules
    private val _installedModules = MutableStateFlow(
        listOf(
            ModuleItem("v4a", "ViPER4Android FX", "v2.9.1", "Team ViPER", "Advanced audio enhancement engine for Android.", true, false),
            ModuleItem("fonts", "Google Sans Font Pack", "v1.3", "Magisk Team", "Replaces system fonts with clean Google Sans typography.", true, false),
            ModuleItem("busybox", "BusyBox for Android NDK", "v1.36.1", "osm0sis", "Full-featured UNIX utilities binary.", true, false)
        )
    )
    val installedModules: StateFlow<List<ModuleItem>> = _installedModules.asStateFlow()

    private val _onlineModules = MutableStateFlow(
        listOf(
            ModuleItem("youtube_revanced", "YouTube ReVanced Magisk", "v19.16", "ReVanced Team", "Ad-free YouTube with background playback.", false, true, "https://github.com/revanced/revanced-magisk"),
            ModuleItem("pixel_spoof", "Pixel 8 Pro Feature Enabler", "v4.0", "PixelMods", "Unlocks Google Pixel AI features globally.", false, true, "https://github.com/pixelmods/spoof"),
            ModuleItem("cgroup_tweaks", "LKT Performance Tweaks", "v2.5", "Bouih", "Advanced CPU governor and battery life optimizer.", false, true, "https://github.com/bouih/lkt")
        )
    )
    val onlineModules: StateFlow<List<ModuleItem>> = _onlineModules.asStateFlow()

    // Boot Scripts
    private val _bootScripts = MutableStateFlow(
        listOf(
            BootScript("post-fs-data.sh", "/data/adb/post-fs-data.sh", "#!/system/bin/sh\n# Executed in early boot before data mount\nmount -o rw,remount /system", true),
            BootScript("service.sh", "/data/adb/service.sh", "#!/system/bin/sh\n# Executed in late_start service mode\necho \"Magisk Service Started\" > /data/local/tmp/magisk_status.log", true)
        )
    )
    val bootScripts: StateFlow<List<BootScript>> = _bootScripts.asStateFlow()

    // Terminal & Logs
    private val _terminalLogs = MutableStateFlow(
        mutableListOf(
            LogEntry(1, getTimeString(), LogLevel.INFO, "Magisk", "Magisk v27.0 daemon initialized."),
            LogEntry(2, getTimeString(), LogLevel.SU, "su", "root@android:/ # whoami\nroot (UID 0)")
        )
    )
    val terminalLogs: StateFlow<List<LogEntry>> = _terminalLogs.asStateFlow()

    private val _appLogs = MutableStateFlow(
        mutableListOf(
            LogEntry(1, getTimeString(), LogLevel.INFO, "Magisk", "Magisk v27.0 daemon started successfully."),
            LogEntry(2, getTimeString(), LogLevel.INFO, "Zygisk", "Zygisk injection active in 52 running processes."),
            LogEntry(3, getTimeString(), LogLevel.WARN, "DenyList", "Enforced hiding on target packages."),
            LogEntry(4, getTimeString(), LogLevel.SU, "Superuser", "Granted su permission to Termux (UID 10234).")
        )
    )
    val appLogs: StateFlow<List<LogEntry>> = _appLogs.asStateFlow()

    // Actions
    fun toggleEmergencyUnroot() {
        val newState = !_isEmergencyUnrooted.value
        _isEmergencyUnrooted.update { newState }
        addAppLog(LogLevel.WARN, "Emergency", if (newState) "Emergency Unroot Active! Root temporarily disabled." else "Root restored successfully.")
    }

    fun toggleZygisk() {
        _zygiskEnabled.update { !it }
        addAppLog(LogLevel.INFO, "Zygisk", "Zygisk status changed to: ${_zygiskEnabled.value}")
    }

    fun toggleDenyListActive() {
        _denylistActive.update { !it }
        addAppLog(LogLevel.INFO, "DenyList", "DenyList global state: ${_denylistActive.value}")
    }

    fun toggleAmoledTheme() {
        _amoledTheme.update { !it }
    }

    fun setSuAccessMode(mode: String) {
        _suAccessMode.value = mode
        addAppLog(LogLevel.INFO, "Superuser", "Superuser access mode set to: $mode")
    }

    fun repackageApp(customName: String) {
        val randomPkg = "com.manager.tool.${(1000..9999).random()}"
        _packageNameStatus.value = randomPkg
        _hiddenIconActive.value = true
        addAppLog(LogLevel.INFO, "Repackage", "Magisk successfully repackaged as '$customName' ($randomPkg)")
    }

    fun toggleSuperuserAccess(packageName: String) {
        _superuserApps.update { list ->
            list.map { if (it.packageName == packageName) it.copy(isAllowed = !it.isAllowed) else it }
        }
    }

    fun applyDenyTemplate(templateType: String) {
        _denyApps.update { list ->
            list.map { app ->
                when (templateType) {
                    "banking" -> if (app.category == "Banking" || app.category == "Finance") app.copy(isHidden = true) else app
                    "games" -> if (app.category == "Games") app.copy(isHidden = true) else app
                    "all" -> app.copy(isHidden = true)
                    else -> app
                }
            }
        }
        addAppLog(LogLevel.INFO, "DenyList", "Applied hiding template: $templateType")
    }

    fun toggleAppDeny(packageName: String) {
        _denyApps.update { list ->
            list.map { if (it.packageName == packageName) it.copy(isHidden = !it.isHidden) else it }
        }
    }

    fun executeTerminalCommand(cmd: String) {
        val trimmed = cmd.trim()
        val time = getTimeString()
        val output = when {
            trimmed == "whoami" -> "root"
            trimmed == "id" -> "uid=0(root) gid=0(root) groups=0(root)"
            trimmed == "magisk -v" -> "Magisk v27.0 (27000) - TopJohnWu"
            trimmed.startsWith("pm ") -> "Package operation completed successfully for: ${trimmed.substringAfter(" ")}"
            trimmed.startsWith("su") -> "Granted root access to shell session."
            trimmed == "reboot recovery" -> "Rebooting device into recovery mode..."
            trimmed == "reboot" -> "Rebooting system..."
            else -> "shell@android:/ \$ $trimmed\nExecuted successfully without errors."
        }

        _terminalLogs.update { list ->
            val mutable = list.toMutableList()
            mutable.add(LogEntry(System.currentTimeMillis(), time, LogLevel.SU, "cmd", "$ $trimmed\n$output"))
            mutable
        }
    }

    fun updateBootScript(path: String, newContent: String) {
        _bootScripts.update { list ->
            list.map { if (it.path == path) it.copy(content = newContent) else it }
        }
        addAppLog(LogLevel.INFO, "Scripts", "Updated boot script: $path")
    }

    fun toggleBootScript(path: String) {
        _bootScripts.update { list ->
            list.map { if (it.path == path) it.copy(isEnabled = !it.isEnabled) else it }
        }
    }

    fun installModule(module: ModuleItem) {
        _onlineModules.update { list -> list.filter { it.id != module.id } }
        _installedModules.update { list -> list + module.copy(isOnline = false) }
        addAppLog(LogLevel.INFO, "Modules", "Successfully flashed module: ${module.name}")
    }

    fun uninstallModule(id: String) {
        _installedModules.update { list -> list.filter { it.id != id } }
        addAppLog(LogLevel.INFO, "Modules", "Uninstalled module ID: $id")
    }

    fun addAppLog(level: LogLevel, tag: String, message: String) {
        _appLogs.update { list ->
            val mutable = list.toMutableList()
            mutable.add(0, LogEntry(System.currentTimeMillis(), getTimeString(), level, tag, message))
            mutable
        }
    }

    private fun getTimeString(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }
}
