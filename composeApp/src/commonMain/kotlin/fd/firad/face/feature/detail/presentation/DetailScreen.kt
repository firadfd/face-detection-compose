package fd.firad.face.feature.detail.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fd.firad.face.core.localization.LocalAppStrings
import fd.firad.face.core.ui.components.AppButton
import fd.firad.face.core.ui.components.AppText

@Composable
fun DetailScreen(onBack: () -> Unit) {
    val strings = LocalAppStrings.current
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppText(strings.detailScreen, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            AppButton(
                text = strings.back,
                onClick = onBack
            )
        }
    }
}
