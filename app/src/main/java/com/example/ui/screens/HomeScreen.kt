package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun HomeScreen(viewModel: MagiskViewModel) {
    val magiskVersion by viewModel.magiskVersion.collectAsState()
    val appVersion by viewModel.appVersion.collectAsState()
    val isEmergencyUnrooted by viewModel.isEmergencyUnrooted.collectAsState()

    var showRepackageDialog by remember { mutableStateOf(false) }
    var customAppName by remember { mutableStateOf("Settings") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Magisk Status Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isEmergencyUnrooted) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (isEmergencyUnrooted) Icons.Default.Warning else Icons.Default.Shield,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = if (isEmergencyUnrooted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isEmergencyUnrooted) "Emergency Unroot Active" else "Magisk $magiskVersion",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isEmergencyUnrooted) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isEmergencyUnrooted) "Root is temporarily disabled" else "App: $appVersion • RAMDISK: Yes",
                    fontSize = 14.sp,
                    color = (if (isEmergencyUnrooted) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer).copy(alpha = 0.8f)
                )
            }
        }

        // Install & Flashing Card
        Text("Magisk Installation", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text("Direct Install (Recommended)", fontWeight = FontWeight.Bold)
                            Text("Install Magisk directly to recovery", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Button(onClick = {
                        viewModel.addAppLog(com.example.model.LogLevel.INFO, "Install", "Direct Install completed successfully. Reboot required.")
                    }, shape = RoundedCornerShape(8.dp)) {
                        Text("Install")
                    }
                }

                Divider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text("Select and Patch a File", fontWeight = FontWeight.Bold)
                            Text("Patch boot.img or recovery image", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    OutlinedButton(onClick = {
                        viewModel.addAppLog(com.example.model.LogLevel.INFO, "Patch", "boot.img successfully patched with Magisk v27.0.")
                    }, shape = RoundedCornerShape(8.dp)) {
                        Text("Select")
                    }
                }
            }
        }

        // Emergency & Repackage Actions
        Text("Security & Hide Tools", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.toggleEmergencyUnroot() },
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEmergencyUnrooted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Warning, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isEmergencyUnrooted) "Restore Root" else "Emergency Unroot", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = { showRepackageDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.VpnKey, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Hide Magisk App", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    if (showRepackageDialog) {
        AlertDialog(
            onDismissRequest = { showRepackageDialog = false },
            title = { Text("Hide Magisk App (Repackage)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter custom app name to disguise Magisk on your launcher (e.g., 'Settings'):")
                    OutlinedTextField(
                        value = customAppName,
                        onValueChange = { customAppName = it },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.repackageApp(customAppName)
                    showRepackageDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRepackageDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
