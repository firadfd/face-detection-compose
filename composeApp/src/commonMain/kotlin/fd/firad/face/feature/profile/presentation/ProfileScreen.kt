package fd.firad.face.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fd.firad.face.core.localization.LocalAppStrings
import fd.firad.face.core.theme.AppThemeMode
import fd.firad.face.core.theme.ThemeViewModel
import org.koin.compose.koinInject

@Composable
fun ProfileScreen() {
    val themeViewModel = koinInject<ThemeViewModel>()
    val currentTheme by themeViewModel.themeMode.collectAsState()
    val currentLang by themeViewModel.language.collectAsState()
    val strings = LocalAppStrings.current

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

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
