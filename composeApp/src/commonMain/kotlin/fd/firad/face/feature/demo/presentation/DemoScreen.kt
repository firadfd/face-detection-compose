package fd.firad.face.feature.demo.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.camera.CAMERA
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.gallery.GALLERY
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import fd.firad.face.core.util.LocalNotifier
import fd.firad.face.core.localization.LocalAppStrings
import fd.firad.face.core.ui.components.AppButton
import fd.firad.face.core.ui.components.AppText
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
fun DemoScreen(onNavigateToNext: () -> Unit) {
    val viewModel = koinViewModel<DemoViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    
    val scope = rememberCoroutineScope()
    val localNotifier = koinInject<LocalNotifier>()
    val strings = LocalAppStrings.current
    
    // MOKO Permissions
    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }
    BindEffect(controller)

    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays: List<ByteArray> ->
            byteArrays.firstOrNull()?.let {
                viewModel.updateSelectedImage(it.toImageBitmap())
            }
        }
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppText(strings.kmpStackDemo, style = MaterialTheme.typography.headlineMedium)

            // Kotlinx DateTime
            AppButton(
                text = strings.showCurrentTime,
                onClick = {
                    val now = Clock.System.now()
                    val timeString = now.toLocalDateTime(TimeZone.currentSystemDefault()).toString()
                    viewModel.updateTime(timeString)
                }
            )
            if (uiState.currentTime.isNotEmpty()) {
                AppText("${strings.timeLabel}${uiState.currentTime}")
            }

            HorizontalDivider()

            // Ktor & Serialization
            AppButton(
                text = if (uiState.isFetching) strings.fetching else strings.fetchFromKtor,
                isLoading = uiState.isFetching,
                onClick = {
                    viewModel.fetchPost(1)
                }
            )
            AppText(uiState.postResult.ifEmpty { strings.clickToFetch })

            HorizontalDivider()

            // MOKO Permissions
            AppButton(
                text = strings.requestPermissions,
                onClick = {
                    scope.launch {
                        try {
                            viewModel.updatePermissionStatus(strings.requestingCamera)
                            controller.providePermission(Permission.CAMERA)
                            viewModel.updatePermissionStatus(strings.cameraGranted)
                            controller.providePermission(Permission.GALLERY)
                            viewModel.updatePermissionStatus(strings.galleryGranted)
                            viewModel.updatePermissionStatus(strings.requestingNotifications)
                            controller.providePermission(Permission.REMOTE_NOTIFICATION)
                            viewModel.updatePermissionStatus(strings.allPermissionsGranted)
                        } catch (e: Exception) {
                            viewModel.updatePermissionStatus("Permission Error: ${e.message}")
                        }
                    }
                }
            )
            if (uiState.permissionStatus.isNotEmpty()) {
                AppText(uiState.permissionStatus)
            }

            // Image Picker
            AppButton(
                text = strings.pickImage,
                onClick = {
                    singleImagePicker.launch()
                }
            )
            uiState.selectedImage?.let {
                Image(
                    bitmap = it,
                    contentDescription = "Selected Image",
                    modifier = Modifier.size(200.dp)
                )
            }

            HorizontalDivider()

            // Local Notification (Firebase-free)
            AppButton(
                text = strings.sendLocalNotification,
                onClick = {
                    scope.launch {
                        try {
                            controller.providePermission(Permission.REMOTE_NOTIFICATION)
                            localNotifier.notify("Face Detection", "This is a local notification without Firebase!")
                        } catch (e: Exception) {
                            println("Permission Error: ${e.message}")
                        }
                    }
                }
            )

            HorizontalDivider()

            // Navigation
            AppButton(
                text = strings.goToNextScreen,
                onClick = onNavigateToNext
            )
        }
    }
}
