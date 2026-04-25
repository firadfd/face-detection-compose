package fd.firad.face.features.demo.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import kotlin.time.Clock

@Serializable
data class Post(val userId: Int, val id: Int, val title: String, val body: String)



@Composable
fun MainScreen(onNavigateToNext: () -> Unit) {
    val scope = rememberCoroutineScope()
    val client = koinInject<HttpClient>()
    val localNotifier = koinInject<LocalNotifier>()
    val strings = LocalAppStrings.current
    
    var ktorResult by remember { mutableStateOf("") }
    if (ktorResult.isEmpty()) {
        ktorResult = strings.clickToFetch
    }
    var currentTime by remember { mutableStateOf("") }
    var isFetching by remember { mutableStateOf(false) }
    
    // MOKO Permissions
    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }
    BindEffect(controller)

    var selectedImage by remember { mutableStateOf<ImageBitmap?>(null) }
    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays: List<ByteArray> ->
            byteArrays.firstOrNull()?.let {
                selectedImage = it.toImageBitmap()
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                currentTime = now.toLocalDateTime(TimeZone.currentSystemDefault()).toString()
            }
        )
        if (currentTime.isNotEmpty()) {
            AppText("${strings.timeLabel}$currentTime")
        }

        HorizontalDivider()

        // Ktor & Serialization
        AppButton(
            text = if (isFetching) strings.fetching else strings.fetchFromKtor,
            isLoading = isFetching,
            onClick = {
                scope.launch {
                    isFetching = true
                    ktorResult = strings.fetching
                    try {
                        val post = client.get("https://jsonplaceholder.typicode.com/posts/1").body<Post>()
                        ktorResult = "Title: ${post.title}"
                    } catch (e: Exception) {
                        ktorResult = "Error: ${e.message}"
                    } finally {
                        isFetching = false
                    }
                }
            }
        )
        AppText(ktorResult)

        HorizontalDivider()

        // MOKO Permissions
        AppButton(
            text = strings.requestPermissions,
            onClick = {
                scope.launch {
                    try {
                        ktorResult = strings.requestingCamera
                        controller.providePermission(Permission.CAMERA)
                        ktorResult = strings.cameraGranted
                        controller.providePermission(Permission.GALLERY)
                        ktorResult = strings.galleryGranted
                        ktorResult = strings.requestingNotifications
                        controller.providePermission(Permission.REMOTE_NOTIFICATION)
                        ktorResult = strings.allPermissionsGranted
                    } catch (e: Exception) {
                        ktorResult = "Permission Error: ${e.message}"
                    }
                }
            }
        )

        // Image Picker
        AppButton(
            text = strings.pickImage,
            onClick = {
                singleImagePicker.launch()
            }
        )
        selectedImage?.let {
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
