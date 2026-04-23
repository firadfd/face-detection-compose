package fd.firad.face.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Tab(val title: String, val icon: ImageVector) {
    data object Home : Tab("Home", Icons.Default.Home)
    data object Demo : Tab("Demo", Icons.Default.Settings)
    data object Profile : Tab("Profile", Icons.Default.Person)
}

@Composable
fun DashboardScreen(onNavigateToDetail: () -> Unit) {
    var selectedTab by remember { mutableStateOf<Tab>(Tab.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val tabs = listOf(Tab.Home, Tab.Demo, Tab.Profile)
                tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (selectedTab) {
                Tab.Home -> WelcomeContent()
                Tab.Demo -> MainScreen(onNavigateToNext = onNavigateToDetail)
                Tab.Profile -> ProfileContent()
            }
        }
    }
}

@Composable
fun WelcomeContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Home Screen", style = MaterialTheme.typography.headlineLarge)
        Text("Welcome to the Dashboard!")
    }
}

@Composable
fun ProfileContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile Screen", style = MaterialTheme.typography.headlineLarge)
        Text("User Profile Info Here")
    }
}
