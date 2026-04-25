package fd.firad.face.feature.verify.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import fd.firad.face.core.localization.LocalAppStrings
import fd.firad.face.core.ui.components.AppButton
import fd.firad.face.core.ui.components.AppText
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FaceVerifyScreen() {
    val viewModel = koinViewModel<FaceVerifyViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()

    var pickerTarget by remember { mutableIntStateOf(0) }
    val pickerLauncher = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let { bytes ->
                val bitmap = bytes.toImageBitmap()
                if (pickerTarget == 1) {
                    viewModel.onImage1Selected(bytes, bitmap)
                } else {
                    viewModel.onImage2Selected(bytes, bitmap)
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        AppText(
            text = "Face Verification with MobileFaceNet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ImagePickerBox(
                label = "First Face",
                image = uiState.imageBitmap1,
                modifier = Modifier.weight(1f),
                onClick = {
                    pickerTarget = 1
                    pickerLauncher.launch()
                }
            )
            ImagePickerBox(
                label = "Second Face",
                image = uiState.imageBitmap2,
                modifier = Modifier.weight(1f),
                onClick = {
                    pickerTarget = 2
                    pickerLauncher.launch()
                }
            )
        }

        uiState.similarity?.let { similarity ->
            SimilarityResult(similarity)
        }

        if (uiState.errorMessage != null) {
            Text(
                text = "Error: ${uiState.errorMessage}",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        AppButton(
            text = if (uiState.isVerifying) "Verifying..." else "Verify Similarity",
            enabled = uiState.image1 != null && uiState.image2 != null && !uiState.isVerifying,
            isLoading = uiState.isVerifying,
            onClick = { viewModel.verify() },
            modifier = Modifier.fillMaxWidth()
        )
        
        TextButton(onClick = { viewModel.reset() }) {
            Text("Reset")
        }
    }
}

@Composable
fun ImagePickerBox(
    label: String,
    image: androidx.compose.ui.graphics.ImageBitmap?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (image != null) {
                Image(
                    bitmap = image,
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        AppText(text = label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun SimilarityResult(similarity: Float) {
    val percentage = (similarity * 100).toInt()
    val isMatch = similarity > 0.8f
    val color = if (isMatch) Color(0xFF4CAF50) else Color(0xFFF44336)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppText(
                text = if (isMatch) "Match Found!" else "No Match",
                style = MaterialTheme.typography.titleLarge,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            AppText(
                text = "Similarity: $percentage%",
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { similarity },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
        }
    }
}
