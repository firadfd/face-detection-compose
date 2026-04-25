package fd.firad.face.feature.liveverify.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.preat.peekaboo.ui.camera.CameraMode
import com.preat.peekaboo.ui.camera.PeekabooCamera
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.camera.CAMERA
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import fd.firad.face.core.localization.LocalAppStrings
import fd.firad.face.core.ui.components.AppButton
import fd.firad.face.core.ui.components.AppText
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LiveVerifyScreen() {
    val viewModel = koinViewModel<LiveVerifyViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()

    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }
    BindEffect(controller)

    var permissionState by remember { mutableStateOf(PermissionState.NotDetermined) }

    LaunchedEffect(Unit) {
        permissionState = controller.getPermissionState(Permission.CAMERA)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (permissionState) {
            PermissionState.Granted -> {
                LiveVerifyContent(viewModel, uiState)
            }
            PermissionState.DeniedAlways -> {
                PermissionDeniedContent("Camera permission is permanently denied. Please enable it in settings.")
            }
            else -> {
                PermissionRequestContent {
                    scope.launch {
                        try {
                            controller.providePermission(Permission.CAMERA)
                            permissionState = PermissionState.Granted
                        } catch (e: Exception) {
                            permissionState = PermissionState.DeniedAlways
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveVerifyContent(
    viewModel: LiveVerifyViewModel,
    uiState: LiveVerifyUiState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (uiState.step == LiveVerifyStep.SCANNING_TO_SAVE || uiState.step == LiveVerifyStep.SCANNING_TO_VERIFY) {
            Box(modifier = Modifier.fillMaxSize()) {
                PeekabooCamera(
                    modifier = Modifier.fillMaxSize(),
                    cameraMode = CameraMode.Front,
                    captureIcon = { onClick ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Scanner Overlay (Shadow + Mesh)
                            ScannerOverlay(isScanning = true)
                            
                            // Top "Cancel" Button
                            TextButton(
                                onClick = { viewModel.clearResult() },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(top = 48.dp, start = 16.dp)
                            ) {
                                Text("Cancel", color = Color(0xFF007AFF), fontSize = 18.sp, fontWeight = FontWeight.Medium)
                            }

                            // Shutter Button
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 40.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .border(4.dp, Color(0xFF00FF88), CircleShape)
                                        .padding(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .clickable { if (!uiState.isScanning) onClick() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .border(2.dp, Color.Black.copy(alpha = 0.1f), CircleShape)
                                    )
                                }
                            }

                            // Scanning Status
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(top = 300.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Scanning...",
                                    color = Color(0xFF00FF88),
                                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp),
                                    modifier = Modifier.alpha(if (uiState.isScanning) 1f else 0.5f)
                                )
                                
                                if (uiState.isScanning) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    CircularProgressIndicator(
                                        color = Color(0xFF00FF88),
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    },
                    onCapture = { bytes ->
                        bytes?.let { viewModel.onImageCaptured(it) }
                    }
                )
            }
        } else {
            // Initial or Completed State (Themed Black)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val icon = when {
                    uiState.step == LiveVerifyStep.COMPLETED && uiState.isSuccess == true -> Icons.Default.CheckCircle
                    uiState.step == LiveVerifyStep.COMPLETED && uiState.isSuccess == false -> Icons.Default.Error
                    uiState.savedEmbedding != null -> Icons.Default.CameraAlt
                    else -> Icons.Default.CameraAlt
                }
                
                val iconColor = when {
                    uiState.step == LiveVerifyStep.COMPLETED && uiState.isSuccess == true -> Color(0xFF4CAF50)
                    uiState.step == LiveVerifyStep.COMPLETED && uiState.isSuccess == false -> Color(0xFFF44336)
                    else -> Color.White
                }

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = iconColor
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = when {
                        uiState.step == LiveVerifyStep.COMPLETED -> if (uiState.isSuccess == true) "Verification Success" else "Verification Failed"
                        uiState.savedEmbedding != null -> "Ready to Verify"
                        else -> "Setup Face ID"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                uiState.message?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (uiState.isSuccess == false) Color(0xFFF44336) else Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }

                if (uiState.similarity != null) {
                    Text(
                        text = "Similarity Score: ${(uiState.similarity * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                AppButton(
                    text = when {
                        uiState.step == LiveVerifyStep.COMPLETED -> "Try Again"
                        uiState.savedEmbedding != null -> "Start Verification"
                        else -> "Get Started"
                    },
                    onClick = { viewModel.startScanning() },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.White,
                    contentColor = Color.Black
                )

                if (uiState.savedEmbedding != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = { viewModel.reset() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFF3B30))
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reset Face Data")
                    }
                }
            }
        }
    }
}

@Composable
fun ScannerOverlay(isScanning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    
    // Pulse effect
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Mesh point movement
    val meshMove by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // 3D-like rotation angle
    val meshRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Scanning line position
    val scanLinePos by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    ) {
        val circleRadius = size.width * 0.38f * pulseScale
        val center = Offset(size.width / 2, size.height * 0.45f)
        
        // 1. Semi-transparent background with cutout
        drawRect(color = Color.Black.copy(alpha = 0.75f))
        drawCircle(
            color = Color.Transparent,
            radius = circleRadius,
            center = center,
            blendMode = BlendMode.Clear
        )
        
        // 2. Segmented Neon Green Border
        val strokeWidth = 4.dp.toPx()
        val segmentCount = 70
        val segmentGap = 2.5f
        val segmentLength = (360f / segmentCount) - segmentGap
        
        for (i in 0 until segmentCount) {
            val startAngle = i * (segmentLength + segmentGap)
            drawArc(
                color = Color(0xFF00FF88),
                startAngle = startAngle,
                sweepAngle = segmentLength,
                useCenter = false,
                topLeft = Offset(center.x - circleRadius, center.y - circleRadius),
                size = Size(circleRadius * 2, circleRadius * 2),
                style = Stroke(width = strokeWidth),
                alpha = 0.6f
            )
        }
        
        // 3. Futuristic 3D-Rotating Face Mesh + Moving Scan Line
        clipPath(Path().apply { addOval(Rect(center, circleRadius)) }) {
            val meshColor = Color(0xFF00FF88)
            
            // --- Scan Line (Top to Bottom) ---
            val lineY = center.y - circleRadius + (circleRadius * 2 * scanLinePos)
            val lineBrush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    meshColor.copy(alpha = 0.1f),
                    meshColor.copy(alpha = 0.5f),
                    meshColor.copy(alpha = 0.1f),
                    Color.Transparent
                )
            )
            drawRect(
                brush = lineBrush,
                topLeft = Offset(center.x - circleRadius, lineY - 20.dp.toPx()),
                size = Size(circleRadius * 2, 40.dp.toPx()),
                alpha = 0.6f
            )
            drawLine(
                color = meshColor,
                start = Offset(center.x - circleRadius * 0.9f, lineY),
                end = Offset(center.x + circleRadius * 0.9f, lineY),
                strokeWidth = 2.dp.toPx(),
                alpha = 0.8f
            )
            
            // --- 3D Mesh ---
            val rotationRad = (meshRotation * 3.14159f / 180f)
            val basePoints = listOf(
                Offset(0f, -circleRadius * 0.6f),
                Offset(-circleRadius * 0.35f, -circleRadius * 0.35f),
                Offset(circleRadius * 0.35f, -circleRadius * 0.35f),
                Offset(-circleRadius * 0.55f, 0f),
                Offset(circleRadius * 0.55f, 0f),
                Offset(-circleRadius * 0.35f, circleRadius * 0.35f),
                Offset(circleRadius * 0.35f, circleRadius * 0.35f),
                Offset(0f, circleRadius * 0.65f),
                Offset(-circleRadius * 0.22f, -circleRadius * 0.12f),
                Offset(circleRadius * 0.22f, -circleRadius * 0.12f)
            )

            val transformedPoints = basePoints.map { p ->
                val cosRot = kotlin.math.cos(rotationRad + p.y * 0.01f)
                val rotatedX = p.x * cosRot
                Offset(center.x + rotatedX, center.y + p.y + meshMove)
            }

            transformedPoints.forEachIndexed { i, p1 ->
                transformedPoints.forEachIndexed { j, p2 ->
                    val distance = (p1 - p2).getDistance()
                    if (i < j && distance < circleRadius * 0.75f) {
                        drawLine(
                            color = meshColor,
                            start = p1,
                            end = p2,
                            strokeWidth = 1.dp.toPx(),
                            alpha = 0.25f * (1f - distance / (circleRadius * 0.75f))
                        )
                    }
                }
            }

            transformedPoints.forEach { p ->
                drawCircle(color = meshColor, radius = 2.5.dp.toPx(), center = p, alpha = 0.5f)
            }

            val chinMove = meshMove * 2
            val chinPath = Path().apply {
                moveTo(center.x - circleRadius * 0.7f, center.y + circleRadius * 0.5f + chinMove)
                quadraticBezierTo(
                    center.x, center.y + circleRadius * 0.85f + chinMove,
                    center.x + circleRadius * 0.7f, center.y + circleRadius * 0.5f + chinMove
                )
            }
            drawPath(path = chinPath, color = meshColor, style = Stroke(width = 2.dp.toPx()), alpha = 0.7f)
        }
        
        // 4. Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF00FF88).copy(alpha = 0.12f), Color.Transparent),
                center = center,
                radius = circleRadius + 40.dp.toPx()
            ),
            radius = circleRadius + 40.dp.toPx(),
            center = center
        )
    }
}

@Composable
fun PermissionRequestContent(onRequest: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        AppText("Camera Permission Required", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        AppText("We need camera access to perform live face verification.", textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        AppButton("Grant Permission", onClick = onRequest, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun PermissionDeniedContent(message: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Error, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(24.dp))
        AppText("Permission Denied", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        AppText(message, textAlign = TextAlign.Center)
    }
}
