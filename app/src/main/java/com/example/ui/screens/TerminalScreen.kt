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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MagiskViewModel

@Composable
fun TerminalScreen(viewModel: MagiskViewModel) {
    val terminalLogs by viewModel.terminalLogs.collectAsState()
    val bootScripts by viewModel.bootScripts.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var commandInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Terminal & Scripts",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Terminal") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Boot Scripts") })
        }

        if (selectedTab == 0) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(terminalLogs) { log ->
                        Text(
                            text = log.message,
                            color = Color(0xFF00FF66),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(onClick = { viewModel.executeTerminalCommand("whoami") }, modifier = Modifier.weight(1f)) { Text("whoami", fontSize = 11.sp) }
                OutlinedButton(onClick = { viewModel.executeTerminalCommand("magisk -v") }, modifier = Modifier.weight(1f)) { Text("version", fontSize = 11.sp) }
                OutlinedButton(onClick = { viewModel.executeTerminalCommand("id") }, modifier = Modifier.weight(1f)) { Text("id", fontSize = 11.sp) }
                OutlinedButton(onClick = { viewModel.executeTerminalCommand("reboot recovery") }, modifier = Modifier.weight(1f)) { Text("recovery", fontSize = 11.sp) }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commandInput,
                    onValueChange = { commandInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Enter shell command...") },
                    singleLine = true
                )
                Button(onClick = {
                    if (commandInput.isNotBlank()) {
                        viewModel.executeTerminalCommand(commandInput)
                        commandInput = ""
                    }
                }, shape = RoundedCornerShape(8.dp)) {
                    Icon(Icons.Default.Send, contentDescription = "Run")
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(bootScripts) { script ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(script.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(script.path, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(checked = script.isEnabled, onCheckedChange = { viewModel.toggleBootScript(script.path) })
                            }
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(8.dp)) {
                                Text(script.content, modifier = Modifier.padding(12.dp), fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
