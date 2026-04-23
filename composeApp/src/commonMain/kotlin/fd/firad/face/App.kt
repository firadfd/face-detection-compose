package fd.firad.face

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.tooling.preview.Preview
import fd.firad.face.navigation.NavGraph
import fd.firad.face.navigation.Route
import fd.firad.face.ui.theme.AppTheme
import kotlinx.serialization.json.Json

@Composable
@Preview
fun App() {
    AppTheme {
        val backStack = rememberSaveable(saver = listSaver(
            save = { stateList -> stateList.map { Json.encodeToString<Route>(it) } },
            restore = { savedList -> savedList.map { Json.decodeFromString<Route>(it) }.toMutableStateList() }
        )) { 
            mutableStateListOf<Route>(Route.Login) 
        }
        
        NavGraph(backStack = backStack)
    }
}