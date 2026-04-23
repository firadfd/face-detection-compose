package fd.firad.face

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import fd.firad.face.screens.DetailScreen
import fd.firad.face.screens.LoginScreen
import fd.firad.face.screens.DashboardScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.compose.KoinContext

@Serializable
sealed interface Route {
    @Serializable data object Login : Route
    @Serializable data object Dashboard : Route
    @Serializable data object Detail : Route
}

@Composable
@Preview
fun App() {
    KoinContext {
        MaterialTheme {
            val backStack = rememberSaveable(saver = listSaver(
                save = { stateList -> stateList.map { Json.encodeToString<Route>(it) } },
                restore = { savedList -> savedList.map { Json.decodeFromString<Route>(it) }.toMutableStateList() }
            )) { 
                mutableStateListOf<Route>(Route.Login) 
            }
            
            NavDisplay(
                backStack = backStack,
                onBack = { if (backStack.size > 1) backStack.removeLast() }
            ) { route ->
                when (route) {
                    Route.Login -> NavEntry(route) {
                        LoginScreen(onLoginSuccess = {
                            backStack.clear()
                            backStack.add(Route.Dashboard)
                        })
                    }
                    Route.Dashboard -> NavEntry(route) {
                        DashboardScreen(onNavigateToDetail = {
                            backStack.add(Route.Detail)
                        })
                    }
                    Route.Detail -> NavEntry(route) { 
                        DetailScreen(onBack = { backStack.removeLast() }) 
                    }
                }
            }
        }
    }
}