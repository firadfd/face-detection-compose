package fd.firad.face.features.dashboard.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import fd.firad.face.core.localization.*
import fd.firad.face.core.theme.*
import fd.firad.face.core.util.*
import fd.firad.face.features.demo.presentation.MainScreen
import org.koin.compose.koinInject

sealed class Tab(val icon: ImageVector, val title: (AppStrings) -> String) {
    data object Home : Tab(Icons.Default.Home, { it.home })
    data object Demo : Tab(Icons.Default.Settings, { it.demo })
    data object Profile : Tab(Icons.Default.Person, { it.profile })
}

@Composable
fun DashboardScreen(onNavigateToDetail: () -> Unit) {
    val tabs = listOf(Tab.Home, Tab.Demo, Tab.Profile)
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val selectedTab = tabs[selectedTabIndex]
    val strings = LocalAppStrings.current

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            tabs.forEachIndexed { index, tab ->
                item(
                    icon = { Icon(tab.icon, contentDescription = tab.title(strings)) },
                    label = { Text(tab.title(strings)) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
    val windowSize = rememberWindowSizeClass()
    val strings = LocalAppStrings.current
    
    if (windowSize.isExpanded || windowSize.isMedium) {
        // Large screen layout: Side-by-side
        Row(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(strings.welcome, style = MaterialTheme.typography.displayMedium)
                Text(strings.loginContinue, style = MaterialTheme.typography.headlineSmall)
            }
            Spacer(modifier = Modifier.width(64.dp))
            Card(modifier = Modifier.size(300.dp)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Adaptive Illustration", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    } else {
        // Mobile layout: Stacked
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(strings.welcome, style = MaterialTheme.typography.headlineLarge)
            Text(strings.loginContinue)
        }
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
