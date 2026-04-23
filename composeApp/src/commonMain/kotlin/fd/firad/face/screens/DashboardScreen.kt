package fd.firad.face.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import fd.firad.face.ui.theme.*
import org.koin.compose.koinInject

sealed class Tab(val title: String, val icon: ImageVector) {
    data object Home : Tab("Home", Icons.Default.Home)
    data object Demo : Tab("Demo", Icons.Default.Settings)
    data object Profile : Tab("Profile", Icons.Default.Person)
}

@Composable
fun DashboardScreen(onNavigateToDetail: () -> Unit) {
    val tabs = listOf(Tab.Home, Tab.Demo, Tab.Profile)
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val selectedTab = tabs[selectedTabIndex]

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
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
    val strings = LocalAppStrings.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(strings.welcome, style = MaterialTheme.typography.headlineLarge)
        Text(strings.loginContinue)
    }
}

@Composable
fun ProfileContent() {
    val themeViewModel = koinInject<ThemeViewModel>()
    val currentTheme by themeViewModel.themeMode.collectAsState()
    val currentLang by themeViewModel.language.collectAsState()
    val strings = LocalAppStrings.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = strings.profile,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(text = strings.theme, style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeOption(AppThemeMode.LIGHT, strings.light, currentTheme) { themeViewModel.setTheme(it) }
            ThemeOption(AppThemeMode.DARK, strings.dark, currentTheme) { themeViewModel.setTheme(it) }
            ThemeOption(AppThemeMode.SYSTEM, strings.system, currentTheme) { themeViewModel.setTheme(it) }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = strings.language, style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LanguageOption("en", "English", currentLang) { themeViewModel.setLanguage(it) }
            LanguageOption("bn", "বাংলা", currentLang) { themeViewModel.setLanguage(it) }
            LanguageOption("ar", "العربية", currentLang) { themeViewModel.setLanguage(it) }
        }
    }
}

@Composable
fun ThemeOption(mode: AppThemeMode, label: String, current: AppThemeMode, onClick: (AppThemeMode) -> Unit) {
    FilterChip(
        selected = current == mode,
        onClick = { onClick(mode) },
        label = { Text(label) }
    )
}

@Composable
fun LanguageOption(code: String, label: String, current: String, onClick: (String) -> Unit) {
    FilterChip(
        selected = current == code,
        onClick = { onClick(code) },
        label = { Text(label) }
    )
}
