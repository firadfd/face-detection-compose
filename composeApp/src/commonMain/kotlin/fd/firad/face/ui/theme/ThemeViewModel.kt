package fd.firad.face.ui.theme

import androidx.lifecycle.ViewModel
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel(private val settings: Settings) : ViewModel() {
    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _language = MutableStateFlow("en")
    val language: StateFlow<String> = _language.asStateFlow()

    init {
        val savedTheme = settings.getString("theme_mode", AppThemeMode.SYSTEM.name)
        _themeMode.value = AppThemeMode.valueOf(savedTheme)
        
        _language.value = settings.getString("app_language", "en")
    }

    fun setTheme(mode: AppThemeMode) {
        _themeMode.value = mode
        settings.putString("theme_mode", mode.name)
    }

    fun setLanguage(lang: String) {
        _language.value = lang
        settings.putString("app_language", lang)
    }
}
