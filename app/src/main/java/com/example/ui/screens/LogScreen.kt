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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LogLevel
import com.example.viewmodel.MagiskViewModel

@Composable
fun LogScreen(viewModel: MagiskViewModel) {
    val appLogs by viewModel.appLogs.collectAsState()
    val amoledTheme by viewModel.amoledTheme.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<LogLevel?>(null) }
    var selectedTab by remember { mutableStateOf(0) }

    val filteredLogs = appLogs.filter { log ->
        val matchesSearch = log.message.contains(searchQuery, ignoreCase = true) || log.tag.contains(searchQuery, ignoreCase = true)
        val matchesLevel = selectedFilter == null || log.level == selectedFilter
        matchesSearch && matchesLevel
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Logs & Settings",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Magisk Logs") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Settings") })
        }

        if (selectedTab == 0) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search logs...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = selectedFilter == null, onClick = { selectedFilter = null }, label = { Text("All") })
                FilterChip(selected = selectedFilter == LogLevel.INFO, onClick = { selectedFilter = LogLevel.INFO }, label = { Text("Info") })
                FilterChip(selected = selectedFilter == LogLevel.WARN, onClick = { selectedFilter = LogLevel.WARN }, label = { Text("Warn") })
                FilterChip(selected = selectedFilter == LogLevel.SU, onClick = { selectedFilter = LogLevel.SU }, label = { Text("SU") })
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredLogs) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "[${log.timestamp}] ${log.tag}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(text = log.level.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(text = log.message, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                        }
                    }
                }
            }
        } else {
            // Settings Tab
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Magisk Settings", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("AMOLED Black Theme", fontWeight = FontWeight.Bold)
                            Text("Pure black background for OLED screens", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = amoledTheme, onCheckedChange = { viewModel.toggleAmoledTheme() })
                    }

                    Divider()

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Magisk Version", fontWeight = FontWeight.Bold)
                        Text("v27.0 (27000)", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Superuser Access", fontWeight = FontWeight.Bold)
                        Text("Apps & Services managed via Magisk Su", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
