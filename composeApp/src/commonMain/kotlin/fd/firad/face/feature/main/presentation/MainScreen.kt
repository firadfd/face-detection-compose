package fd.firad.face.feature.main.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import fd.firad.face.core.localization.AppStrings
import fd.firad.face.core.localization.LocalAppStrings
import fd.firad.face.core.ui.components.AppText
import fd.firad.face.feature.demo.presentation.DemoScreen
import fd.firad.face.feature.home.presentation.HomeScreen
import fd.firad.face.feature.profile.presentation.ProfileScreen

sealed class Tab(val icon: ImageVector, val title: (AppStrings) -> String) {
    data object Home : Tab(Icons.Default.Home, { it.home })
    data object Demo : Tab(Icons.Default.Settings, { it.demo })
    data object Profile : Tab(Icons.Default.Person, { it.profile })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onNavigateToDetail: () -> Unit) {
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = Color.Unspecified,
                        navigationIconContentColor = Color.Unspecified,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = Color.Unspecified
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
                    Tab.Home -> HomeScreen()
                    Tab.Demo -> DemoScreen(onNavigateToNext = onNavigateToDetail)
                    Tab.Profile -> ProfileScreen()
                }
            }
        }
    }
}
