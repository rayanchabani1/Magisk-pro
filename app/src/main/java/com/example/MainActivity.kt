package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MagiskViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Superuser : Screen("superuser", "Superuser", Icons.Default.AdminPanelSettings)
    object Modules : Screen("modules", "Modules", Icons.Default.Extension)
    object Deny : Screen("deny", "DenyList", Icons.Default.Security)
    object Tools : Screen("tools", "Tools", Icons.Default.Terminal)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MagiskViewModel = viewModel()
            val amoledTheme by viewModel.amoledTheme.collectAsState()

            MyApplicationTheme(amoledBlack = amoledTheme) {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
                val items = listOf(Screen.Home, Screen.Superuser, Screen.Modules, Screen.Deny, Screen.Tools)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                                    label = { Text(screen.title) },
                                    selected = currentScreen == screen,
                                    onClick = { currentScreen = screen }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (currentScreen) {
                            Screen.Home -> HomeScreen(viewModel)
                            Screen.Superuser -> SuperuserScreen(viewModel)
                            Screen.Modules -> ModuleScreen(viewModel)
                            Screen.Deny -> DenyScreen(viewModel)
                            Screen.Tools -> LogScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}
