package fd.firad.face.core.util

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.Composable

@Composable
fun rememberWindowAdaptiveInfo(): WindowAdaptiveInfo = currentWindowAdaptiveInfo()

data class AppWindowSizeClass(
    val isCompact: Boolean,
    val isMedium: Boolean,
    val isExpanded: Boolean
)

@Composable
fun rememberWindowSizeClass(): AppWindowSizeClass {
    val info = currentWindowAdaptiveInfo()
    val sizeClass = info.windowSizeClass
    
    val isExpanded = sizeClass.isWidthAtLeastBreakpoint(840)
    val isMedium = sizeClass.isWidthAtLeastBreakpoint(600) && !isExpanded
    val isCompact = !isMedium && !isExpanded
    
    return AppWindowSizeClass(
        isCompact = isCompact,
        isMedium = isMedium,
        isExpanded = isExpanded
    )
}
