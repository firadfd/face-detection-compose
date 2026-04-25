package fd.firad.face.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import fd.firad.face.feature.main.presentation.MainScreen
import fd.firad.face.feature.detail.presentation.DetailScreen
import fd.firad.face.feature.auth.presentation.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object Login : Route
    @Serializable data object Main : Route
    @Serializable data object Detail : Route
}

@Composable
fun NavGraph(
    backStack: SnapshotStateList<Route>
) {
    NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeLast() }
    ) { route ->
        when (route) {
            Route.Login -> NavEntry(route) {
                LoginScreen(onLoginSuccess = {
                    backStack.clear()
                    backStack.add(Route.Main)
                })
            }
            Route.Main -> NavEntry(route) {
                MainScreen(onNavigateToDetail = {
                    backStack.add(Route.Detail)
                })
            }
            Route.Detail -> NavEntry(route) { 
                DetailScreen(onBack = { backStack.removeLast() }) 
            }
        }
    }
}
