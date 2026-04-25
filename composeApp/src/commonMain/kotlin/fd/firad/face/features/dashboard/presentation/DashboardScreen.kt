package fd.firad.face.features.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fd.firad.face.core.localization.AppStrings
import fd.firad.face.core.localization.LocalAppStrings
import fd.firad.face.core.theme.AppThemeMode
import fd.firad.face.core.theme.ThemeViewModel
import fd.firad.face.core.ui.components.AppButton
import fd.firad.face.core.ui.components.AppText
import fd.firad.face.features.demo.presentation.MainScreen
import fd.firad.face.features.employees.presentation.EmployeeList
import fd.firad.face.features.employees.presentation.EmployeeSkeletonList
import fd.firad.face.features.employees.presentation.EmployeesUiState
import fd.firad.face.features.employees.presentation.EmployeesViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

sealed class Tab(val icon: ImageVector, val title: (AppStrings) -> String) {
    data object Home : Tab(Icons.Default.Home, { it.home })
    data object Demo : Tab(Icons.Default.Settings, { it.demo })
    data object Profile : Tab(Icons.Default.Person, { it.profile })
}

@OptIn(ExperimentalMaterial3Api::class)
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
                    label = { AppText(tab.title(strings)) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        AppText(
                            text = selectedTab.title(strings),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (selectedTab) {
                    Tab.Home -> WelcomeContent()
                    Tab.Demo -> MainScreen(onNavigateToNext = onNavigateToDetail)
                    Tab.Profile -> ProfileContent()
                }
            }
        }
    }
}




@Composable
fun WelcomeContent() {
    val viewModel = koinViewModel<EmployeesViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current

    Column(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            when (val state = uiState) {
                is EmployeesUiState.Loading -> {
                    EmployeeSkeletonList()
                }
                is EmployeesUiState.Success -> {
                    EmployeeList(employees = state.employees)
                }
                is EmployeesUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AppText(
                            text = "Error: ${state.message}", 
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        AppButton(
                            text = "Retry",
                            onClick = { viewModel.loadEmployees() }
                        )
                    }
                }
                else -> {}
            }
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

        AppText(text = strings.theme, style = MaterialTheme.typography.titleMedium)
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
