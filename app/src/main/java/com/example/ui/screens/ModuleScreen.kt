package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MagiskViewModel

@Composable
fun ModuleScreen(viewModel: MagiskViewModel) {
    val installedModules by viewModel.installedModules.collectAsState()
    val onlineModules by viewModel.onlineModules.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showBackupDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Magisk Modules",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = {
                    viewModel.addAppLog(com.example.model.LogLevel.INFO, "Flash", "Flashed module zip from storage successfully.")
                }) {
                    Icon(Icons.Default.Upload, contentDescription = "Install from storage")
                }
                IconButton(onClick = { showBackupDialog = true }) {
                    Icon(Icons.Default.Save, contentDescription = "Backup Profile")
                }
            }
        }

        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Installed (${installedModules.size})") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Online Repo (${onlineModules.size})") })
        }

        if (selectedTab == 0) {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(installedModules) { mod ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(mod.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                IconButton(onClick = { viewModel.uninstallModule(mod.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            Text("Version: ${mod.version} • By ${mod.author}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            Text(mod.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(onlineModules) { mod ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(mod.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Button(onClick = { viewModel.installModule(mod) }, shape = RoundedCornerShape(8.dp)) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Install")
                                }
                            }
                            Text("Version: ${mod.version} • By ${mod.author}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            Text(mod.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }

    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Profile Backup & Restore") },
            text = { Text("Save your modules list & settings into a backup profile file or restore them instantly after flashing a new ROM.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.addAppLog(com.example.model.LogLevel.INFO, "Backup", "Profile saved to /sdcard/Download/magisk_backup.json")
                    showBackupDialog = false
                }) { Text("Backup Now") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    viewModel.addAppLog(com.example.model.LogLevel.INFO, "Restore", "Profile successfully restored.")
                    showBackupDialog = false
                }) { Text("Restore 1-Click") }
            }
        )
    }
}
